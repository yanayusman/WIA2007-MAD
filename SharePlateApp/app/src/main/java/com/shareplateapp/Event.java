package com.shareplateapp;

public abstract class Event {

    private final String title;

    private final String description;
    private final String location;
    private final String date; // maybe we can change this datatype from "String" to "Date"
    private final int imageResourceId;

    public Event(String title, String desc, String loc, String date, int image){

        description = desc;
        location = loc;
        this.date = date;
        this.title = title;
        imageResourceId = image;

    }

    public String getDescription(){
        return description;
    }

    public String getLocation(){
        return location;
    }

    public String getDate(){
        return date;
    }

    public String getTitle(){
        return title;
    }

    public int getImageResourceId(){
        return imageResourceId;
    }

}
