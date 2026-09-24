package com.smarthotel.model;
public record Room(int id,String roomNumber,String category,double price,int capacity,String facilities,String description,String status,String photoPath) {
    public Room(int id,String roomNumber,String category,double price,int capacity,String facilities,String description,String status) {
        this(id,roomNumber,category,price,capacity,facilities,description,status,null);
    }
}
