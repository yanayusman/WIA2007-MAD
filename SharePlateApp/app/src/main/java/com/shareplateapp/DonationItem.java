package com.shareplateapp;

import java.io.Serializable;

public class DonationItem implements Serializable {
    private String name, foodCategory, expiredDate, quantity, pickupTime, location;
    private int imageResourceId;

    // Constructor
    public DonationItem(String name, String foodCategory, String expiredDate, String quantity, String pickupTime, String location, int imageResourceId) {
        this.name = name;
        this.foodCategory = foodCategory;
        this.expiredDate = expiredDate;
        this.quantity = quantity;
        this.pickupTime = pickupTime;
        this.location = location;
        this.imageResourceId = imageResourceId;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getFoodCategory() {
        return foodCategory;
    }

    public String getExpiredDate(){ return expiredDate; }

    public String getQuantity(){ return quantity; }

    public String getPickupTime(){ return pickupTime; }

    public String getLocation() {
        return location;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}