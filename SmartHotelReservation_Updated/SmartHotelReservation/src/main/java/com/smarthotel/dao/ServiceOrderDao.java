package com.smarthotel.dao;
import com.smarthotel.database.Database;
import com.smarthotel.model.ServiceOrder;
import java.sql.*; import java.util.*;
public class ServiceOrderDao {
    private ServiceOrder map(ResultSet r)throws SQLException{return new ServiceOrder(r.getInt("id"),r.getInt("reservation_id"),r.getInt("service_id"),r.getString("service_name"),r.getDouble("unit_price"),r.getInt("quantity"),r.getDouble("amount"),r.getString("status"),r.getString("message"));}
    public List<ServiceOrder> byReservation(int rid)throws SQLException{return query("SELECT so.*,s.name service_name,s.price unit_price FROM service_orders so JOIN services s ON s.id=so.service_id WHERE so.reservation_id=? ORDER BY so.id DESC",rid);}
    public List<ServiceOrder> all()throws SQLException{List<ServiceOrder>x=new ArrayList<>();try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("SELECT so.*,s.name service_name,s.price unit_price FROM service_orders so JOIN services s ON s.id=so.service_id ORDER BY so.id DESC");ResultSet r=p.executeQuery()){while(r.next())x.add(map(r));}return x;}
    private List<ServiceOrder> query(String q,int rid)throws SQLException{List<ServiceOrder>x=new ArrayList<>();try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement(q)){p.setInt(1,rid);try(ResultSet r=p.executeQuery()){while(r.next())x.add(map(r));}}return x;}
    public void request(int rid,int sid,int qty)throws SQLException{if(qty<1)throw new IllegalArgumentException("Quantity must be at least 1.");try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("INSERT INTO service_orders(reservation_id,service_id,quantity,amount,status,message) SELECT ?,?,? ,price*?,'PENDING','Waiting for admin approval' FROM services WHERE id=? AND active=1")){p.setInt(1,rid);p.setInt(2,sid);p.setInt(3,qty);p.setInt(4,qty);p.setInt(5,sid);if(p.executeUpdate()==0)throw new IllegalArgumentException("Service is unavailable.");}}
    public void setDecision(int id,String status,String message)throws SQLException{if(!Set.of("APPROVED","REJECTED","PENDING").contains(status))throw new IllegalArgumentException("Invalid service status.");try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("UPDATE service_orders SET status=?,message=? WHERE id=?")){p.setString(1,status);p.setString(2,message);p.setInt(3,id);p.executeUpdate();}}
}
