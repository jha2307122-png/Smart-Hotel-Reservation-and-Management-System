package com.smarthotel.model;
public record ServiceOrder(int id,int reservationId,int serviceId,String serviceName,double unitPrice,int quantity,double amount,String status,String message) {}
