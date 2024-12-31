package com.shareplateapp;

// This class represents a single notification item.
// It stores the data for each notification, such as the title, message, timestamp, and an icon resource ID.

public class Notification {
    private String title;
    private String message;
    private String timestamp;
    private int iconResId;

    // Constructor
    public Notification(String title, String message, String timestamp, int iconResId) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.iconResId = iconResId;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getIconResId() {
        return iconResId;
    }
}
