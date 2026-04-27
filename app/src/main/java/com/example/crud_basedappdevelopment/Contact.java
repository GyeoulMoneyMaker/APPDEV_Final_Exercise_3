package com.example.crud_basedappdevelopment;

public class Contact {
    private int id;
    private String name;
    private String phoneNumber;
    private String dateAdded;
    private boolean isFavorite;

    public Contact(int id, String name, String phoneNumber, String dateAdded, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.dateAdded = dateAdded;
        this.isFavorite = isFavorite;
    }

    public Contact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.isFavorite = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getDateAdded() { return dateAdded; }
    public void setDateAdded(String dateAdded) { this.dateAdded = dateAdded; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}
