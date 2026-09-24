package com.smarthotel.model;
public record Payment(int id,int reservationId,double amount,String method,String status,String reference,String paidAt) {}
