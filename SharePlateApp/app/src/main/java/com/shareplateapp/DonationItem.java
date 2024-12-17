package com.shareplateapp;

public class DonationItem {
    private String name;
    private String description;
    private String distance;
    private int imageResourceId;

    // Constructor
    public DonationItem(String name, String description, String distance, int imageResourceId) {
        this.name = name;
        this.description = description;
        this.distance = distance;
        this.imageResourceId = imageResourceId;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDistance() {
        return distance;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}