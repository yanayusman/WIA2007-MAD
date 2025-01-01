package com.shareplateapp;

public class Campaign extends Event {
    
    public Campaign(String name, String desc, String date, String time, String typeOfEvents, 
                   String seatAvailable, String location, int img, String imageUrl, String ownerImageUrl) {
        super(name, desc, date, time, typeOfEvents, seatAvailable, location, img, imageUrl, ownerImageUrl);
    }

    // Add any Campaign-specific methods or fields here
}
