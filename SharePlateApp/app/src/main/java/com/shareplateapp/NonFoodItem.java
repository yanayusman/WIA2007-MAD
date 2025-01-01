package com.shareplateapp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NonFoodItem implements Serializable {

    private String name;
    private String category;
    private String description;
    private String quantity;
    private String pickupTime;
    private String location;
    private int imageResourceId;
    private String imageUrl;
    private String ownerUsername;
    private String documentId;
    private String status; // "active" or "completed"
    private long createdAt;
    private String ownerProfileImageUrl;
    private String donateType;

    public NonFoodItem(String name, String category, String description, String quantity,
                       String pickupTime, String location, int imageResourceId, String imageUrl,
                       String ownerUsername, String ownerProfileImageUrl, String donateType) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.quantity = quantity;
        this.pickupTime = pickupTime;
        this.location = location;
        this.imageResourceId = imageResourceId;
        this.imageUrl = imageUrl;
        this.ownerUsername = ownerUsername;
        this.ownerProfileImageUrl = ownerProfileImageUrl;
        this.donateType = donateType;
        this.status = "active";
        this.createdAt = System.currentTimeMillis();
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
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

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getFormattedCreationDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.US);
        return sdf.format(new Date(createdAt));
    }

    public String getOwnerProfileImageUrl() {
        return ownerProfileImageUrl;
    }

    public String getDonateType() {
        return donateType;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
