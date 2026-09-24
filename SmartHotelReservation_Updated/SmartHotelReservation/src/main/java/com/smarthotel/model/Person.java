package com.smarthotel.model;
public abstract class Person { private final int id; private String name; protected Person(int id,String name){this.id=id;this.name=name;} public int getId(){return id;} public String getName(){return name;} public void setName(String n){name=n;} public abstract String role(); }
