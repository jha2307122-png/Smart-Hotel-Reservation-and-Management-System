package com.smarthotel.dao;

import com.smarthotel.database.Database;
import com.smarthotel.model.Reservation;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

public class ReservationDao {
    private Reservation map(ResultSet r)throws SQLException{return new Reservation(r.getInt("id"),r.getInt("customer_id"),r.getInt("room_id"),r.getString("check_in"),r.getString("check_out"),r.getInt("guests"),r.getString("status"),r.getDouble("total"));}
    public int create(int customer,int room,String in,String out,int guests,double total)throws SQLException{
        if(customer<=0||room<=0)throw new IllegalArgumentException("Invalid customer or room.");
        LocalDate checkIn=LocalDate.parse(in),checkOut=LocalDate.parse(out);
        if(!checkIn.isBefore(checkOut))throw new IllegalArgumentException("Check-out must be after check-in.");
        if(guests<1)throw new IllegalArgumentException("Guests must be at least 1.");
        try(Connection c=Database.connect()){
            c.setAutoCommit(false);
            try{
                int capacity;String roomStatus;
                try(PreparedStatement p=c.prepareStatement("SELECT capacity,status FROM rooms WHERE id=?")){p.setInt(1,room);try(ResultSet r=p.executeQuery()){if(!r.next())throw new IllegalArgumentException("Room not found.");capacity=r.getInt(1);roomStatus=r.getString(2);}}
                if("MAINTENANCE".equals(roomStatus))throw new IllegalArgumentException("Room is under maintenance.");
                if(guests>capacity)throw new IllegalArgumentException("Guest count exceeds room capacity.");
                String overlap="SELECT COUNT(*) FROM reservations WHERE room_id=? AND status IN ('PENDING','CONFIRMED','CHECKED_IN') AND date(check_in)<date(?) AND date(check_out)>date(?)";
                try(PreparedStatement p=c.prepareStatement(overlap)){p.setInt(1,room);p.setString(2,out);p.setString(3,in);try(ResultSet r=p.executeQuery()){if(r.next()&&r.getInt(1)>0)throw new IllegalArgumentException("Room is already booked for those dates.");}}
                int id;
                try(PreparedStatement p=c.prepareStatement("INSERT INTO reservations(customer_id,room_id,check_in,check_out,guests,status,total) VALUES(?,?,?,?,?,'CONFIRMED',?)",Statement.RETURN_GENERATED_KEYS)){p.setInt(1,customer);p.setInt(2,room);p.setString(3,in);p.setString(4,out);p.setInt(5,guests);p.setDouble(6,total);p.executeUpdate();try(ResultSet k=p.getGeneratedKeys()){if(!k.next())throw new SQLException("Could not create reservation.");id=k.getInt(1);}}
                c.commit();return id;
            }catch(Exception e){c.rollback();if(e instanceof SQLException se)throw se;if(e instanceof RuntimeException re)throw re;throw new SQLException(e);}finally{c.setAutoCommit(true);}
        }
    }
    public List<Reservation> all()throws SQLException{return query("SELECT * FROM reservations ORDER BY id DESC",null);}
    public List<Reservation> search(String term)throws SQLException{
        String q="SELECT r.* FROM reservations r JOIN customers c ON c.id=r.customer_id JOIN rooms m ON m.id=r.room_id WHERE CAST(r.id AS TEXT) LIKE ? OR lower(c.full_name) LIKE lower(?) OR lower(m.room_number) LIKE lower(?) OR lower(r.status) LIKE lower(?) ORDER BY r.id DESC";
        List<Reservation>x=new ArrayList<>();String t="%"+(term==null?"":term.trim())+"%";try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement(q)){for(int i=1;i<=4;i++)p.setString(i,t);try(ResultSet r=p.executeQuery()){while(r.next())x.add(map(r));}}return x;
    }
    public List<Reservation> byCustomer(int cid)throws SQLException{return query("SELECT * FROM reservations WHERE customer_id=? ORDER BY id DESC",cid);}
    private List<Reservation> query(String sql,Integer cid)throws SQLException{List<Reservation>x=new ArrayList<>();try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement(sql)){if(cid!=null)p.setInt(1,cid);try(ResultSet r=p.executeQuery()){while(r.next())x.add(map(r));}}return x;}
    public void setStatus(int id,String status)throws SQLException{try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("UPDATE reservations SET status=? WHERE id=?")){p.setString(1,status);p.setInt(2,id);p.executeUpdate();}}
    public void modify(int id,String in,String out,int guests,double total)throws SQLException{
        LocalDate a=LocalDate.parse(in),b=LocalDate.parse(out);if(!a.isBefore(b))throw new IllegalArgumentException("Check-out must be after check-in.");
        try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("SELECT room_id FROM reservations WHERE id=?")){p.setInt(1,id);try(ResultSet r=p.executeQuery()){if(!r.next())throw new IllegalArgumentException("Reservation not found.");int room=r.getInt(1);
            try(PreparedStatement q=c.prepareStatement("SELECT COUNT(*) FROM reservations WHERE room_id=? AND id<>? AND status IN ('PENDING','CONFIRMED','CHECKED_IN') AND date(check_in)<date(?) AND date(check_out)>date(?)")){q.setInt(1,room);q.setInt(2,id);q.setString(3,out);q.setString(4,in);try(ResultSet z=q.executeQuery()){if(z.next()&&z.getInt(1)>0)throw new IllegalArgumentException("New dates overlap another booking.");}}
        }}
        try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("UPDATE reservations SET check_in=?,check_out=?,guests=?,total=? WHERE id=?")){p.setString(1,in);p.setString(2,out);p.setInt(3,guests);p.setDouble(4,total);p.setInt(5,id);p.executeUpdate();}
    }
    public void cancel(int id)throws SQLException{setStatus(id,"CANCELLED");releaseRoomIfFree(id);}
    public void checkIn(int id)throws SQLException{setStatus(id,"CHECKED_IN");int room=roomId(id);new RoomDao().setStatus(room,"OCCUPIED");}
    public void checkOut(int id)throws SQLException{setStatus(id,"CHECKED_OUT");int room=roomId(id);new RoomDao().setStatus(room,"AVAILABLE");}
    private int roomId(int id)throws SQLException{try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("SELECT room_id FROM reservations WHERE id=?")){p.setInt(1,id);try(ResultSet r=p.executeQuery()){if(!r.next())throw new IllegalArgumentException("Reservation not found.");return r.getInt(1);}}}
    private void releaseRoomIfFree(int id)throws SQLException{int room=roomId(id);try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("UPDATE rooms SET status='AVAILABLE' WHERE id=? AND status='RESERVED' AND NOT EXISTS(SELECT 1 FROM reservations WHERE room_id=? AND status IN ('PENDING','CONFIRMED','CHECKED_IN'))")){p.setInt(1,room);p.setInt(2,room);p.executeUpdate();}}
    public long nights(String in,String out){return Math.max(1,Duration.between(LocalDate.parse(in).atStartOfDay(),LocalDate.parse(out).atStartOfDay()).toDays());}
    public Reservation byId(int id) throws SQLException {
        try (Connection c = Database.connect();
             PreparedStatement p = c.prepareStatement("SELECT * FROM reservations WHERE id=?")) {
            p.setInt(1, id);
            try (ResultSet r = p.executeQuery()) { return r.next() ? map(r) : null; }
        }
    }
    public double recalculateTotal(int id)throws SQLException{try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("SELECT r.room_id,r.check_in,r.check_out,rm.price,COALESCE((SELECT SUM(amount) FROM service_orders so WHERE so.reservation_id=r.id AND so.status='APPROVED'),0) FROM reservations r JOIN rooms rm ON rm.id=r.room_id WHERE r.id=?")){p.setInt(1,id);try(ResultSet x=p.executeQuery()){if(!x.next())throw new IllegalArgumentException("Reservation not found.");long nights=java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.parse(x.getString(2)),java.time.LocalDate.parse(x.getString(3)));double total=x.getDouble(4)*nights+x.getDouble(5);updateTotal(id,total);return total;}}}
    public void updateTotal(int id,double total)throws SQLException{try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("UPDATE reservations SET total=? WHERE id=?")){p.setDouble(1,total);p.setInt(2,id);p.executeUpdate();}}
}
