package com.smarthotel.model;
public record Review(int id,int customerId,int reservationId,int rating,String comment,String createdAt) {}
