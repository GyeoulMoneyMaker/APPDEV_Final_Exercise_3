package com.example.crud_basedappdevelopment;

public class Contact {
    private int id;
    private String name;
    private String phoneNumber;
    private String dateAdded;

    public Contact(int id, String name, String phoneNumber, String dateAdded) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.dateAdded = dateAdded;
    }

    public Contact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getDateAdded() { return dateAdded; }
    public void setDateAdded(String dateAdded) { this.dateAdded = dateAdded; }
}
