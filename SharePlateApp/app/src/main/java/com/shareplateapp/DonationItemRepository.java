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
                        String ownerUsername = document.getString("ownerUsername");
                        String status = document.getString("status");
                        
                        int imageResourceId = R.drawable.placeholder_image;
                        Long resourceIdLong = document.getLong("imageResourceID");
                        if (resourceIdLong != null) {
                            imageResourceId = resourceIdLong.intValue();
                        }
                        
                        String imageUrl = document.getString("imageUrl");
                    
                        Long createdAt = document.getLong("createdAt");
                        
                        if (name != null && !name.isEmpty()) {
                            DonationItem item = new DonationItem(
                                name,
                                foodCategory != null ? foodCategory : "",
                                expiredDate != null ? expiredDate : "",
                                quantity != null ? quantity : "",
                                pickupTime != null ? pickupTime : "",
                                location != null ? location : "",
                                imageResourceId,
                                imageUrl,
                                ownerUsername != null ? ownerUsername : "Anonymous"
                            );
                            // Set the document ID and status
                            item.setDocumentId(document.getId());
                            item.setStatus(status != null ? status : "active");
                            if (createdAt != null) {
                                item.setCreatedAt(createdAt);
                            }
                            items.add(item);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing document: " + e.getMessage());
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
            donationData.put("ownerUsername", item.getOwnerUsername());
            donationData.put("status", item.getStatus());
            donationData.put("createdAt", System.currentTimeMillis());

            db.collection(COLLECTION_NAME)
                .add(donationData)
                .addOnSuccessListener(documentReference -> {
                    String docId = documentReference.getId();
                    // Set the document ID in the item
                    item.setDocumentId(docId);
                    // No need to update the document with its ID since we can use the Firestore ID
                    System.out.println("Document added with ID: " + docId);
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error adding document: " + e);
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
                    // Add sample data with owner username
                    addDonationItem(new DonationItem(
                        "(sample) Fresh Bread",
                        "Food Item : Bread",
                        "Expires : Nov 29",
                        "5",
                        "Pickup Time : Available by 5 pm",
                        "Petaling Jaya",
                        R.drawable.bread,
                        null,
                        "Sample User" // Add owner username for sample data
                    ));
                }
            });
    }

    public void deleteDonationItem(String documentId, OnDeleteCompleteListener listener) {
        if (documentId == null) {
            System.err.println("Cannot delete item: document ID is null");
            if (listener != null) {
                listener.onDeleteFailure(new Exception("Document ID is null"));
            }
            return;
        }

        System.out.println("Attempting to delete document with ID: " + documentId);
        
        db.collection(COLLECTION_NAME)
            .document(documentId)
            .delete()
            .addOnSuccessListener(aVoid -> {
                System.out.println("Successfully deleted document: " + documentId);
                if (listener != null) {
                    listener.onDeleteSuccess();
                }
            })
            .addOnFailureListener(e -> {
                System.err.println("Failed to delete document: " + documentId + ", error: " + e.getMessage());
                if (listener != null) {
                    listener.onDeleteFailure(e);
                }
            });
    }

    public interface OnDeleteCompleteListener {
        void onDeleteSuccess();
        void onDeleteFailure(Exception e);
    }

    public void updateDonationStatus(String documentId, String status, OnStatusUpdateListener listener) {
        if (documentId == null) {
            if (listener != null) {
                listener.onUpdateFailure(new Exception("Document ID is null"));
            }
            return;
        }

        db.collection(COLLECTION_NAME)
            .document(documentId)
            .update("status", status)
            .addOnSuccessListener(aVoid -> {
                if (listener != null) {
                    listener.onUpdateSuccess();
                }
            })
            .addOnFailureListener(e -> {
                if (listener != null) {
                    listener.onUpdateFailure(e);
                }
            });
    }

    public interface OnStatusUpdateListener {
        void onUpdateSuccess();
        void onUpdateFailure(Exception e);
    }
} 