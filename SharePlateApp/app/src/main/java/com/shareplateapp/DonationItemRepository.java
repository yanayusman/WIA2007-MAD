package com.shareplateapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import android.util.Log;

public class DonationItemRepository {
    private static final String TAG = "DonationItemRepository";
    private static final String COLLECTION_NAME = "allDonationItems";
    private final FirebaseFirestore db;

    // Define the interface for callbacks
    public interface OnDonationCompleteListener {
        void onDonationSuccess();
        void onDonationFailure(Exception e);
    }

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
                        String ownerProfileImageUrl = document.getString("ownerProfileImageUrl");
                        
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
                                ownerUsername != null ? ownerUsername : "Anonymous",
                                ownerProfileImageUrl != null ? ownerProfileImageUrl : ""
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

    public void addDonationItem(DonationItem item, OnDonationCompleteListener listener) {
        // Get current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String ownerUsername = currentUser != null ? currentUser.getDisplayName() : "Anonymous";
        String ownerProfileImageUrl = currentUser != null && currentUser.getPhotoUrl() != null ? 
            currentUser.getPhotoUrl().toString() : "";

        // Create donation data map
        Map<String, Object> donationData = new HashMap<>();
        donationData.put("name", item.getName());
        donationData.put("foodCategory", item.getFoodCategory());
        donationData.put("expiredDate", item.getExpiredDate());
        donationData.put("quantity", item.getQuantity());
        donationData.put("pickupTime", item.getPickupTime());
        donationData.put("location", item.getLocation());
        donationData.put("imageResourceId", item.getImageResourceId());
        donationData.put("imageUrl", item.getImageUrl());
        donationData.put("ownerUsername", ownerUsername);
        donationData.put("ownerProfileImageUrl", ownerProfileImageUrl);
        donationData.put("status", "active");
        donationData.put("createdAt", System.currentTimeMillis());

        // Add to Firestore
        db.collection(COLLECTION_NAME)
            .add(donationData)
            .addOnSuccessListener(documentReference -> {
                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                listener.onDonationSuccess();
            })
            .addOnFailureListener(e -> {
                Log.w(TAG, "Error adding document", e);
                listener.onDonationFailure(e);
            });
    }

    public void populateInitialData() {
        // Check if collection is empty first
        db.collection(COLLECTION_NAME)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots.isEmpty()) {
                    // Add sample data with owner username and profile image
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String ownerProfileImageUrl = currentUser != null && currentUser.getPhotoUrl() != null ? 
                        currentUser.getPhotoUrl().toString() : "";

                    addDonationItem(new DonationItem(
                        "(sample) Fresh Bread",
                        "Food Item : Bread",
                        "Expires : Nov 29",
                        "5",
                        "Pickup Time : Available by 5 pm",
                        "Petaling Jaya",
                        R.drawable.bread,
                        null,
                        "Sample User",
                        ownerProfileImageUrl
                    ), new OnDonationCompleteListener() {
                        @Override
                        public void onDonationSuccess() {
                            // No need to handle success
                        }

                        @Override
                        public void onDonationFailure(Exception e) {
                            System.err.println("Error adding sample data: " + e.getMessage());
                        }
                    });
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

    public void createDonation(DonationItem item) {
        try {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) return;

            // Get profile image URL from Firebase Auth first
            String profileImageUrl = currentUser.getPhotoUrl() != null ? 
                currentUser.getPhotoUrl().toString() : "";
            
            // Set the owner's profile image URL
            item.setOwnerProfileImageUrl(profileImageUrl);
            
            // Create donation data map
            Map<String, Object> donationData = new HashMap<>();
            donationData.put("name", item.getName());
            donationData.put("foodCategory", item.getFoodCategory());
            donationData.put("expiredDate", item.getExpiredDate());
            donationData.put("quantity", item.getQuantity());
            donationData.put("pickupTime", item.getPickupTime());
            donationData.put("location", item.getLocation());
            donationData.put("imageResourceId", item.getImageResourceId());
            donationData.put("imageUrl", item.getImageUrl());
            donationData.put("ownerUsername", item.getOwnerUsername());
            donationData.put("ownerProfileImageUrl", profileImageUrl);
            donationData.put("status", "active");
            donationData.put("createdAt", System.currentTimeMillis());

            // Add to Firestore
            db.collection(COLLECTION_NAME)
                .add(donationData)
                .addOnSuccessListener(documentReference -> {
                    String docId = documentReference.getId();
                    item.setDocumentId(docId);
                    System.out.println("Document added with ID: " + docId);
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error adding document: " + e);
                });

        } catch (Exception e) {
            System.err.println("Error creating donation data: " + e);
        }
    }

    public void addMockData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String ownerUsername = currentUser != null ? currentUser.getDisplayName() : "Anonymous";
        String ownerProfileImageUrl = currentUser != null && currentUser.getPhotoUrl() != null ? 
            currentUser.getPhotoUrl().toString() : "";

        DonationItem item = new DonationItem(
            "Mock Food Item",
            "Groceries",
            "Dec 31, 2024",
            "2 packs",
            "2:00 PM",
            "123 Street",
            R.drawable.placeholder_image,
            null,
            ownerUsername,
            ownerProfileImageUrl
        );

        addDonationItem(item, new OnDonationCompleteListener() {
            @Override
            public void onDonationSuccess() {
                Log.d(TAG, "Mock data added successfully");
            }

            @Override
            public void onDonationFailure(Exception e) {
                Log.e(TAG, "Failed to add mock data", e);
            }
        });
    }
} 