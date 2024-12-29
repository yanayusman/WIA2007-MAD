package com.shareplateapp;

public enum SortDirection {
    ASCENDING("Ascending"),
    DESCENDING("Descending");

    private final String displayName;

    SortDirection(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 