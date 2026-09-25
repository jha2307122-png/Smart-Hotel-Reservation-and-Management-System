package com.smarthotel.dao;
import com.smarthotel.database.Database; import com.smarthotel.model.Review; import java.sql.*; import java.util.*;
public class ReviewDao {
 private Review map(ResultSet r)throws SQLException{return new Review(r.getInt("id"),r.getInt("customer_id"),r.getInt("reservation_id"),r.getInt("rating"),r.getString("comment"),r.getString("created_at"));}
 public List<Review> all()throws SQLException{return query("SELECT * FROM reviews ORDER BY id DESC",null);}
 public Review byReservation(int rid)throws SQLException{List<Review>x=query("SELECT * FROM reviews WHERE reservation_id=?",rid);return x.isEmpty()?null:x.get(0);}
 public void save(int cid,int rid,int rating,String comment)throws SQLException{if(rating<1||rating>5)throw new IllegalArgumentException("Rating must be 1 to 5.");try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("INSERT INTO reviews(customer_id,reservation_id,rating,comment) VALUES(?,?,?,?)")){p.setInt(1,cid);p.setInt(2,rid);p.setInt(3,rating);p.setString(4,comment);p.executeUpdate();}}
 private List<Review> query(String q,Integer rid)throws SQLException{List<Review>x=new ArrayList<>();try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement(q)){if(rid!=null)p.setInt(1,rid);try(ResultSet r=p.executeQuery()){while(r.next())x.add(map(r));}}return x;}
}
