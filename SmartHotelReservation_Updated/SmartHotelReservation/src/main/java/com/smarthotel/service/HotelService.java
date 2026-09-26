package com.smarthotel.service;

import com.smarthotel.database.Database;
import com.smarthotel.dao.*;
import com.smarthotel.model.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class HotelService {
    public final RoomDao rooms=new RoomDao();
    public final CustomerDao customers=new CustomerDao();
    public final ReservationDao reservations=new ReservationDao();
    public final PaymentDao payments=new PaymentDao();

    public void book(Customer c,Room r,LocalDate in,LocalDate out,int guests,String method)throws Exception{
        if(c==null)throw new IllegalArgumentException("Customer profile not found.");
        if(!in.isBefore(out))throw new IllegalArgumentException("Check-out must be after check-in.");
        if(guests>r.capacity())throw new IllegalArgumentException("Guest count exceeds room capacity.");
        long n=reservations.nights(in.toString(),out.toString());
        double total=new BookingBill(r.price(),n,0,0).calculateTotal();
        int reservationId=reservations.create(c.id(),r.id(),in.toString(),out.toString(),guests,total);
        if(method!=null&&!method.isBlank())payments.create(reservationId,total,method,"SIM-"+System.currentTimeMillis());
    }

    public Map<String,Long> stats()throws SQLException{
        Map<String,Long>m=new LinkedHashMap<>();
        try(var c=Database.connect();var s=c.createStatement()){
            m.put("rooms",scalar(s,"SELECT COUNT(*) FROM rooms"));
            m.put("available",scalar(s,"SELECT COUNT(*) FROM rooms WHERE status='AVAILABLE'"));
            m.put("occupied",scalar(s,"SELECT COUNT(*) FROM rooms WHERE status='OCCUPIED'"));
            m.put("reservations",scalar(s,"SELECT COUNT(*) FROM reservations"));
            m.put("checkins",scalar(s,"SELECT COUNT(*) FROM reservations WHERE check_in=date('now','localtime') AND status IN ('CONFIRMED','CHECKED_IN')"));
            m.put("checkouts",scalar(s,"SELECT COUNT(*) FROM reservations WHERE check_out=date('now','localtime') AND status IN ('CONFIRMED','CHECKED_IN','CHECKED_OUT')"));
            m.put("customers",scalar(s,"SELECT COUNT(*) FROM customers"));
            m.put("payments",scalar(s,"SELECT COUNT(*) FROM payments"));
            m.put("revenue",Math.round(doubleScalar(s,"SELECT COALESCE(SUM(amount),0) FROM payments")));
        }
        return m;
    }
    private long scalar(java.sql.Statement s,String q)throws SQLException{try(var r=s.executeQuery(q)){return r.next()?r.getLong(1):0;}}
    private double doubleScalar(java.sql.Statement s,String q)throws SQLException{try(var r=s.executeQuery(q)){return r.next()?r.getDouble(1):0;}}
}
