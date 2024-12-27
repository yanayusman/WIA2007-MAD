package com.shareplateapp;

public class DonationItem {
    private String name, foodCategory, expiredDate, quantity, pickupTime, distance;
    private int imageResourceId;

    // Constructor
    public DonationItem(String name, String foodCategory, String expiredDate, String quantity, String pickupTime, String distance, int imageResourceId) {
        this.name = name;
        this.foodCategory = foodCategory;
        this.expiredDate = expiredDate;
        this.quantity = quantity;
        this.pickupTime = pickupTime;
        this.distance = distance;
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

    public String getDistance() {
        return distance;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}