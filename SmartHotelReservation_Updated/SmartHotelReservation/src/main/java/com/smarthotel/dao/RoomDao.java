package com.smarthotel.dao;
import com.smarthotel.database.Database;
import com.smarthotel.model.Room;
import java.sql.*;
import java.util.*;

public class RoomDao {
    private Room map(ResultSet r)throws SQLException{return new Room(r.getInt("id"),r.getString("room_number"),r.getString("category"),r.getDouble("price"),r.getInt("capacity"),r.getString("facilities"),r.getString("description"),r.getString("status"),r.getString("photo_path"));}
    public List<Room> all()throws SQLException{return find("",null,null,null);}
    public List<Room> allForAdmin()throws SQLException{List<Room>x=new ArrayList<>();try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("SELECT * FROM rooms ORDER BY room_number");ResultSet r=p.executeQuery()){while(r.next())x.add(map(r));}return x;}
    public List<Room> find(String category,String in,String out,Integer guests)throws SQLException{
        List<Room>x=new ArrayList<>();StringBuilder q=new StringBuilder("SELECT * FROM rooms WHERE status <> 'MAINTENANCE'");List<Object>a=new ArrayList<>();
        if(category!=null&&!category.isBlank()&&!"All".equals(category)){q.append(" AND category=?");a.add(category);}
        if(guests!=null&&guests>0){q.append(" AND capacity>=?");a.add(guests);}
        if(in!=null&&out!=null&&!in.isBlank()&&!out.isBlank()){q.append(" AND NOT EXISTS (SELECT 1 FROM reservations r WHERE r.room_id=rooms.id AND r.status IN ('PENDING','CONFIRMED','CHECKED_IN') AND date(r.check_in)<date(?) AND date(r.check_out)>date(?))");a.add(out);a.add(in);}
        else q.append(" AND status='AVAILABLE'");
        q.append(" ORDER BY room_number");
        try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement(q.toString())){for(int i=0;i<a.size();i++)p.setObject(i+1,a.get(i));try(ResultSet r=p.executeQuery()){while(r.next())x.add(map(r));}}return x;
    }
    public void save(Room r)throws SQLException{
        String q=r.id()==0?"INSERT INTO rooms(room_number,category,price,capacity,facilities,description,status,photo_path) VALUES(?,?,?,?,?,?,?,?)":"UPDATE rooms SET room_number=?,category=?,price=?,capacity=?,facilities=?,description=?,status=?,photo_path=? WHERE id=?";
        try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement(q)){p.setString(1,r.roomNumber());p.setString(2,r.category());p.setDouble(3,r.price());p.setInt(4,r.capacity());p.setString(5,r.facilities());p.setString(6,r.description());p.setString(7,r.status());p.setString(8,r.photoPath());if(r.id()!=0)p.setInt(9,r.id());p.executeUpdate();}
    }
    public void delete(int id)throws SQLException{try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("DELETE FROM rooms WHERE id=?")){p.setInt(1,id);p.executeUpdate();}}
    public Room byId(int id)throws SQLException{try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("SELECT * FROM rooms WHERE id=?")){p.setInt(1,id);try(ResultSet r=p.executeQuery()){return r.next()?map(r):null;}}}
    public void setStatus(int id,String status)throws SQLException{try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("UPDATE rooms SET status=? WHERE id=?")){p.setString(1,status);p.setInt(2,id);p.executeUpdate();}}
}
