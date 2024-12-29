package com.shareplateapp;

import java.io.Serializable;

public class DonationItem implements Serializable {
    private String name;
    private String foodCategory;
    private String expiredDate;
    private String quantity;
    private String pickupTime;
    private String location;
    private int imageResourceId;
    private String imageUrl;

    // Constructor
    public DonationItem(String name, String foodCategory, String expiredDate, String quantity, String pickupTime, String location, int imageResourceId, String imageUrl) {
        this.name = name;
        this.foodCategory = foodCategory;
        this.expiredDate = expiredDate;
        this.quantity = quantity;
        this.pickupTime = pickupTime;
        this.location = location;
        this.imageResourceId = imageResourceId;
        this.imageUrl = imageUrl;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getFoodCategory() {
        return foodCategory;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public String getLocation() {
        return location;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}