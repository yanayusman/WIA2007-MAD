package com.shareplateapp;

import java.io.Serializable;
public class Event implements Serializable {

    private String name, desc, date, time, typeOfEvents, seatAvailable, location;

    private final int imageResourceId;

    public Event(String name, String desc, String date, String time, String typeOfEvents, String seatAvailable, String location, int img){

        this.name = name;
        this.desc = desc;
        this.date = date;
        this.time = time;
        this.typeOfEvents = typeOfEvents;
        this.seatAvailable = seatAvailable;
        this.location = location;
        this.imageResourceId = img;

    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return desc;
    }

    public String getDate(){
        return date;
    }

    public String getTime(){
        return time;
    }

    public String getTypeOfEvents(){
        return typeOfEvents;
    }

    public String getSeatAvailable(){
        return seatAvailable;
    }

    public String getLocation(){
        return location;
    }

    public int getImageResourceId(){
        return imageResourceId;
    }

}
