package com.smarthotel.model;
public record Reservation(int id,int customerId,int roomId,String checkIn,String checkOut,int guests,String status,double total) {}
