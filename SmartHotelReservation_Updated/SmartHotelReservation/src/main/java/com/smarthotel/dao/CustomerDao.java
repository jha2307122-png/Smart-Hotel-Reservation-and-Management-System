package com.smarthotel.dao;

import com.smarthotel.database.Database;
import com.smarthotel.model.Customer;
import java.sql.*;
import java.util.*;

public class CustomerDao {
    private Customer map(ResultSet r) throws SQLException {
        return new Customer(r.getInt("id"), r.getInt("user_id"), r.getString("full_name"), r.getString("email"), r.getString("phone"), r.getString("address"));
    }
    public void create(int userId, String name, String email, String phone, String address) throws SQLException {
        try (Connection c=Database.connect(); PreparedStatement p=c.prepareStatement("INSERT INTO customers(user_id,full_name,email,phone,address) VALUES(?,?,?,?,?)")) {
            p.setInt(1,userId); p.setString(2,name); p.setString(3,email); p.setString(4,phone); p.setString(5,address); p.executeUpdate();
        }
    }
    public Customer byUser(int uid) throws SQLException { return one("SELECT * FROM customers WHERE user_id=?", uid); }
    public Customer byId(int id) throws SQLException { return one("SELECT * FROM customers WHERE id=?", id); }
    private Customer one(String sql,int id) throws SQLException {
        try(Connection c=Database.connect(); PreparedStatement p=c.prepareStatement(sql)){p.setInt(1,id);try(ResultSet r=p.executeQuery()){return r.next()?map(r):null;}}
    }
    public List<Customer> all() throws SQLException { return search(""); }
    public List<Customer> search(String term) throws SQLException {
        List<Customer> x=new ArrayList<>();
        String q="SELECT * FROM customers WHERE lower(full_name) LIKE lower(?) OR lower(email) LIKE lower(?) OR COALESCE(phone,'') LIKE ? ORDER BY full_name";
        try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement(q)){String t="%"+(term==null?"":term.trim())+"%";p.setString(1,t);p.setString(2,t);p.setString(3,t);try(ResultSet r=p.executeQuery()){while(r.next())x.add(map(r));}}return x;
    }
    public void update(int id,String name,String email,String phone,String address)throws SQLException{
        try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("UPDATE customers SET full_name=?,email=?,phone=?,address=? WHERE id=?")){p.setString(1,name);p.setString(2,email);p.setString(3,phone);p.setString(4,address);p.setInt(5,id);p.executeUpdate();}
    }
    public void delete(int id)throws SQLException{
        try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("DELETE FROM customers WHERE id=?")){p.setInt(1,id);p.executeUpdate();}
    }
}
