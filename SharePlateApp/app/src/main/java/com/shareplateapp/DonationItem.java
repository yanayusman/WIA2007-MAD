package com.shareplateapp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DonationItem implements Serializable {
    private String name, foodCategory, description, category, expiredDate, quantity, 
                   pickupTime, location, imageUrl, ownerUsername, documentId, status, 
                   ownerProfileImageUrl, donateType;
    private int imageResourceId;
    private long createdAt;

    // Empty constructor for Firestore
    public DonationItem() {}

    public DonationItem(String name, String foodCategory, String description, String category,
                       String expiredDate, String quantity, String pickupTime, String location,
                       int imageResourceId, String imageUrl, String ownerUsername, String donateType,
                       String ownerProfileImageUrl) {
        this.name = name;
        this.foodCategory = foodCategory;
        this.description = description;
        this.category = category;
        this.expiredDate = expiredDate;
        this.quantity = quantity;
        this.pickupTime = pickupTime;
        this.location = location;
        this.imageResourceId = imageResourceId;
        this.imageUrl = imageUrl;
        this.ownerUsername = ownerUsername;
        this.ownerProfileImageUrl = ownerProfileImageUrl;
        this.status = "active";
        this.createdAt = System.currentTimeMillis();
        this.donateType = donateType;
    }

    // Getters
    public String getName() { return name; }
    public String getFoodCategory() { return foodCategory; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getExpiredDate() { return expiredDate; }
    public String getQuantity() { return quantity; }
    public String getPickupTime() { return pickupTime; }
    public String getLocation() { return location; }
    public int getImageResourceId() { return imageResourceId; }
    public String getImageUrl() { return imageUrl; }
    public String getOwnerUsername() { return ownerUsername; }
    public String getDocumentId() { return documentId; }
    public String getStatus() { return status; }
    public String getOwnerProfileImageUrl() { return ownerProfileImageUrl; }
    public long getCreatedAt() { return createdAt; }
    public String getDonateType() { return donateType; }
    public String getFormattedCreationDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.US);
        return sdf.format(new Date(createdAt));
    }

    // Setters
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
