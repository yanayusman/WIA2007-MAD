package com.shareplateapp;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DonationItemRepository {
    private static final String COLLECTION_NAME = "allDonationItems";
    private final FirebaseFirestore db;

    public DonationItemRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public interface OnDonationItemsLoadedListener {
        void onDonationItemsLoaded(List<DonationItem> items);
        void onError(Exception e);
    }

    public void getAllDonationItems(OnDonationItemsLoadedListener listener) {
        db.collection(COLLECTION_NAME)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<DonationItem> items = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    try {
                        String name = document.getString("name");
                        String foodCategory = document.getString("foodCategory");
                        String expiredDate = document.getString("expiredDate");
                        String quantity = document.getString("quantity");
                        String pickupTime = document.getString("pickupTime");
                        String location = document.getString("location");
                        
                        // Handle potential null values for imageResourceId
                        int imageResourceId = R.drawable.placeholder_image; // Default value
                        Long resourceIdLong = document.getLong("imageResourceID");
                        if (resourceIdLong != null) {
                            imageResourceId = resourceIdLong.intValue();
                        }
                        
                        String imageUrl = document.getString("imageUrl");
                    
                        // Only add if required fields are present
                        if (name != null && !name.isEmpty()) {
                            items.add(new DonationItem(
                                name,
                                foodCategory != null ? foodCategory : "",
                                expiredDate != null ? expiredDate : "",
                                quantity != null ? quantity : "",
                                pickupTime != null ? pickupTime : "",
                                location != null ? location : "",
                                imageResourceId,
                                imageUrl
                            ));
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing document: " + e.getMessage());
                        // Continue to next document
                    }
                }
                listener.onDonationItemsLoaded(items);
            })
            .addOnFailureListener(listener::onError);
    }

    public void addDonationItem(DonationItem item) {
        Map<String, Object> donationData = new HashMap<>();
        try {
            donationData.put("name", item.getName());
            donationData.put("foodCategory", item.getFoodCategory());
            donationData.put("expiredDate", item.getExpiredDate());
            donationData.put("quantity", item.getQuantity());
            donationData.put("pickupTime", item.getPickupTime());
            donationData.put("location", item.getLocation());
            donationData.put("imageResourceID", item.getImageResourceId());
            donationData.put("imageUrl", item.getImageUrl());

            // Log the image URL for debugging
            System.out.println("Saving donation with image URL: " + item.getImageUrl());

            db.collection(COLLECTION_NAME)
                .add(donationData)
                .addOnSuccessListener(documentReference -> {
                    System.out.println("Document added with ID: " + documentReference.getId());
                    System.out.println("Successfully saved donation with image URL: " + item.getImageUrl());
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error adding document: " + e);
                    System.err.println("Failed to save donation with image URL: " + item.getImageUrl());
                });
        } catch (Exception e) {
            System.err.println("Error creating donation data: " + e);
        }
    }

    public void populateInitialData() {
        // Check if collection is empty first
        db.collection(COLLECTION_NAME)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots.isEmpty()) {
                    // Add sample data
                    addDonationItem(new DonationItem(
                        "(sample) Fresh Bread",
                        "Food Item : Bread",
                        "Expires : Nov 29",
                        "5",
                        "Pickup Time : Available by 5 pm",
                        "Petaling Jaya",
                        R.drawable.bread,
                        null
                    ));
                }
            });
    }
} 