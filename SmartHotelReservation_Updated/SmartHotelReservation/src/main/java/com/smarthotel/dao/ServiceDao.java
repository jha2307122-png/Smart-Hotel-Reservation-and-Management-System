package com.smarthotel.dao;
import com.smarthotel.database.Database; import com.smarthotel.model.ServiceItem; import java.sql.*; import java.util.*;
public class ServiceDao {
 public List<ServiceItem> active()throws SQLException{List<ServiceItem>x=new ArrayList<>();try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("SELECT * FROM services WHERE active=1 ORDER BY name");ResultSet r=p.executeQuery()){while(r.next())x.add(new ServiceItem(r.getInt("id"),r.getString("name"),r.getDouble("price"),r.getString("description"),r.getInt("active")==1));}return x;}
}
