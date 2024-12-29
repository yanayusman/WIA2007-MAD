package com.shareplateapp;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NonFoodItemRepository {
    private static final String COLLECTION_NAME = "allNonFoodItems";
    private final FirebaseFirestore db;

    public NonFoodItemRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public interface OnNonFoodItemsLoadedListener {
        void onNonFoodItemsLoaded(List<NonFoodItem> items);
        void onError(Exception e);
    }

    public void getAllNonFoodItems(OnNonFoodItemsLoadedListener listener) {
        db.collection(COLLECTION_NAME)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<NonFoodItem> items = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            String name = document.getString("name");
                            String category = document.getString("category");
                            String description = document.getString("description");
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
                                NonFoodItem item = new NonFoodItem(
                                        name,
                                        category != null ? category : "",
                                        description != null ? description : "",
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
                    listener.onNonFoodItemsLoaded(items);
                })
                .addOnFailureListener(listener::onError);
    }

    public void addNonFoodItem(NonFoodItem item) {
        Map<String, Object> nonFoodData = new HashMap<>();
        try {
            nonFoodData.put("name", item.getName());
            nonFoodData.put("category", item.getCategory());
            nonFoodData.put("description", item.getDescription());
            nonFoodData.put("quantity", item.getQuantity());
            nonFoodData.put("pickupTime", item.getPickupTime());
            nonFoodData.put("location", item.getLocation());
            nonFoodData.put("imageResourceID", item.getImageResourceId());
            nonFoodData.put("imageUrl", item.getImageUrl());
            nonFoodData.put("ownerUsername", item.getOwnerUsername());
            nonFoodData.put("status", item.getStatus());
            nonFoodData.put("createdAt", System.currentTimeMillis());

            db.collection(COLLECTION_NAME)
                    .add(nonFoodData)
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
                        addNonFoodItem(new NonFoodItem(
                                "(sample) T-shirt",
                                "Clothing",
                                "a brand new t-shirt, sized XL",
                                "1",
                                "Available by 5 pm",
                                "KL",
                                R.drawable.bread,
                                null,
                                "Sample User" // Add owner username for sample data
                        ));
                    }
                });
    }

    public void deleteNonFoodItem(String documentId, OnDeleteCompleteListener listener) {
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