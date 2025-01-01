package com.shareplateapp;

import java.io.Serializable;

public class Event implements Serializable {
    private String title;
    private String description;
    private String location;
    private String date;
    private String time;
    private String organizer;
    private String status;
    private String typeOfEvents;
    private String seatAvailable;
    private String imageUrl;
    private String ownerImageUrl;
    private int imageResourceId;

    public Event(String name, String desc, String date, String time, String typeOfEvents, 
                String seatAvailable, String location, int img, String imageUrl, String ownerImageUrl) {
        this.title = name;
        this.description = desc;
        this.date = date;
        this.time = time;
        this.typeOfEvents = typeOfEvents;
        this.seatAvailable = seatAvailable;
        this.location = location;
        this.imageResourceId = img;
        this.imageUrl = imageUrl;
        this.ownerImageUrl = ownerImageUrl;
    }

    // Getters
    public String getName() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getOrganizer() { return organizer; }
    public String getStatus() { return status; }
    public String getTypeOfEvents() { return typeOfEvents; }
    public String getSeatAvailable() { return seatAvailable; }
    public int getImageResourceId() { return imageResourceId; }
    public String getImageUrl() { return imageUrl; }
    public String getOwnerImageUrl() { return ownerImageUrl; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }
    public void setStatus(String status) { this.status = status; }
    public void setTypeOfEvents(String typeOfEvents) { this.typeOfEvents = typeOfEvents; }
    public void setSeatAvailable(String seatAvailable) { this.seatAvailable = seatAvailable; }
    public void setImageResourceId(int imageResourceId) { this.imageResourceId = imageResourceId; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setOwnerImageUrl(String ownerImageUrl) { this.ownerImageUrl = ownerImageUrl; }
}
