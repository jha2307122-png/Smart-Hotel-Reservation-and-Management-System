package com.smarthotel.database;
import com.smarthotel.config.AppConfig; import com.smarthotel.util.PasswordUtil; import java.sql.*;
public final class Database {
 private Database(){}
 public static Connection connect()throws SQLException{Connection c=DriverManager.getConnection(AppConfig.DB_URL);try(Statement s=c.createStatement()){s.execute("PRAGMA foreign_keys=ON");}return c;}
 public static void initialize(){try(Connection c=connect();Statement s=c.createStatement()){
  s.executeUpdate("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE NOT NULL, password_hash TEXT NOT NULL, role TEXT NOT NULL CHECK(role IN ('ADMIN','STAFF','CUSTOMER')), created_at TEXT DEFAULT CURRENT_TIMESTAMP)");
  s.executeUpdate("CREATE TABLE IF NOT EXISTS customers(id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE, full_name TEXT NOT NULL, email TEXT UNIQUE NOT NULL, phone TEXT, address TEXT)");
  s.executeUpdate("CREATE TABLE IF NOT EXISTS rooms(id INTEGER PRIMARY KEY AUTOINCREMENT, room_number TEXT UNIQUE NOT NULL, category TEXT NOT NULL, price REAL NOT NULL, capacity INTEGER NOT NULL, facilities TEXT, description TEXT, status TEXT NOT NULL DEFAULT 'AVAILABLE', photo_path TEXT)");
  s.executeUpdate("CREATE TABLE IF NOT EXISTS reservations(id INTEGER PRIMARY KEY AUTOINCREMENT, customer_id INTEGER NOT NULL REFERENCES customers(id), room_id INTEGER NOT NULL REFERENCES rooms(id), check_in TEXT NOT NULL, check_out TEXT NOT NULL, guests INTEGER NOT NULL, status TEXT NOT NULL DEFAULT 'PENDING', total REAL NOT NULL DEFAULT 0, created_at TEXT DEFAULT CURRENT_TIMESTAMP)");
  s.executeUpdate("CREATE TABLE IF NOT EXISTS payments(id INTEGER PRIMARY KEY AUTOINCREMENT, reservation_id INTEGER NOT NULL REFERENCES reservations(id) ON DELETE CASCADE, amount REAL NOT NULL, method TEXT NOT NULL, status TEXT NOT NULL DEFAULT 'PAID', transaction_ref TEXT, paid_at TEXT DEFAULT CURRENT_TIMESTAMP)");
  s.executeUpdate("CREATE TABLE IF NOT EXISTS staff(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, role TEXT NOT NULL, phone TEXT, email TEXT, salary REAL DEFAULT 0, status TEXT DEFAULT 'ACTIVE')");
  s.executeUpdate("CREATE TABLE IF NOT EXISTS services(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, price REAL NOT NULL, description TEXT, active INTEGER DEFAULT 1)");
  s.executeUpdate("CREATE TABLE IF NOT EXISTS service_orders(id INTEGER PRIMARY KEY AUTOINCREMENT, reservation_id INTEGER NOT NULL REFERENCES reservations(id) ON DELETE CASCADE, service_id INTEGER NOT NULL REFERENCES services(id), quantity INTEGER NOT NULL, amount REAL NOT NULL, status TEXT NOT NULL DEFAULT 'PENDING', message TEXT)");
  s.executeUpdate("CREATE TABLE IF NOT EXISTS coupons(id INTEGER PRIMARY KEY AUTOINCREMENT, code TEXT UNIQUE NOT NULL, discount REAL NOT NULL, active INTEGER DEFAULT 1)");
  s.executeUpdate("CREATE TABLE IF NOT EXISTS reviews(id INTEGER PRIMARY KEY AUTOINCREMENT, customer_id INTEGER NOT NULL REFERENCES customers(id), reservation_id INTEGER REFERENCES reservations(id), rating INTEGER NOT NULL CHECK(rating BETWEEN 1 AND 5), comment TEXT, created_at TEXT DEFAULT CURRENT_TIMESTAMP)");
  migrate(s); seed(s);
 }catch(SQLException e){throw new RuntimeException("Database initialization failed",e);}}
 private static void migrate(Statement s)throws SQLException{
  addColumnIfMissing(s,"rooms","photo_path","TEXT");
  addColumnIfMissing(s,"service_orders","status","TEXT NOT NULL DEFAULT 'PENDING'");
  addColumnIfMissing(s,"service_orders","message","TEXT");
 }
 private static void addColumnIfMissing(Statement s,String table,String col,String def)throws SQLException{try(ResultSet r=s.executeQuery("PRAGMA table_info("+table+")")){while(r.next())if(col.equalsIgnoreCase(r.getString("name")))return;}s.executeUpdate("ALTER TABLE "+table+" ADD COLUMN "+col+" "+def);}
 private static void seed(Statement s)throws SQLException{
  String hash=PasswordUtil.hash("pratik123");
  if(count(s,"users")==0)s.executeUpdate("INSERT INTO users(username,password_hash,role) VALUES('pratik','"+hash+"','ADMIN')");
  else {try(PreparedStatement p=s.getConnection().prepareStatement("UPDATE users SET username='pratik',password_hash=?,role='ADMIN' WHERE role='ADMIN' AND username='admin'")){p.setString(1,hash);p.executeUpdate();}if(countWhere(s,"users","role='ADMIN'")==0)try(PreparedStatement p=s.getConnection().prepareStatement("INSERT OR IGNORE INTO users(username,password_hash,role) VALUES('pratik',?,'ADMIN')")){p.setString(1,hash);p.executeUpdate();}}
  if(count(s,"rooms")==0)s.executeUpdate("INSERT INTO rooms(room_number,category,price,capacity,facilities,description) VALUES ('101','Single',55,1,'Wi-Fi, TV, AC','Comfortable single room'),('201','Double',85,2,'Wi-Fi, TV, AC, Breakfast','Spacious double room'),('301','Deluxe',130,3,'Wi-Fi, TV, AC, Breakfast, Balcony','Premium deluxe room'),('401','Suite',220,4,'Wi-Fi, TV, AC, Breakfast, Living Room, Balcony','Luxury suite')");
  if(count(s,"services")==0)s.executeUpdate("INSERT INTO services(name,price,description) VALUES('Laundry',10,'Laundry service'),('Swimming Pool',8,'Swimming pool access'),('Gym',7,'Gym access'),('Food',20,'Food package'),('Room Service',15,'In-room food service')");
  else {
   insertServiceIfMissing(s,"Laundry",10,"Laundry service"); insertServiceIfMissing(s,"Swimming Pool",8,"Swimming pool access"); insertServiceIfMissing(s,"Gym",7,"Gym access"); insertServiceIfMissing(s,"Food",20,"Food package");
  }
 }
 private static void insertServiceIfMissing(Statement s,String name,double price,String desc)throws SQLException{try(PreparedStatement p=s.getConnection().prepareStatement("INSERT INTO services(name,price,description) SELECT ?,?,? WHERE NOT EXISTS (SELECT 1 FROM services WHERE lower(name)=lower(?))")){p.setString(1,name);p.setDouble(2,price);p.setString(3,desc);p.setString(4,name);p.executeUpdate();}}
 private static int count(Statement s,String t)throws SQLException{try(ResultSet r=s.executeQuery("SELECT COUNT(*) FROM "+t)){return r.next()?r.getInt(1):0;}}
 private static int countWhere(Statement s,String t,String w)throws SQLException{try(ResultSet r=s.executeQuery("SELECT COUNT(*) FROM "+t+" WHERE "+w)){return r.next()?r.getInt(1):0;}}
}
