package com.smarthotel.model;
public class Employee extends Person { private final String jobTitle; public Employee(int id,String name,String jobTitle){super(id,name);this.jobTitle=jobTitle;} public String getJobTitle(){return jobTitle;} @Override public String role(){return jobTitle;} }
