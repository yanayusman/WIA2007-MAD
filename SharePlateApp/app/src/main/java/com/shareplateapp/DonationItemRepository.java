package com.shareplateapp;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                    String name = document.getString("name");
                    String description = document.getString("description");
                    String distance = document.getString("distance");
                    int imageResourceId = document.getLong("imageResourceId").intValue();
                    
                    items.add(new DonationItem(name, description, distance, imageResourceId));
                }
                listener.onDonationItemsLoaded(items);
            })
            .addOnFailureListener(listener::onError);
    }

    public void addDonationItem(DonationItem item) {
        Map<String, Object> donationData = new HashMap<>();
        donationData.put("name", item.getName());
        donationData.put("description", item.getDescription());
        donationData.put("distance", item.getDistance());
        donationData.put("imageResourceId", item.getImageResourceId());

        db.collection(COLLECTION_NAME)
            .add(donationData)
            .addOnSuccessListener(documentReference -> 
                System.out.println("Document added with ID: " + documentReference.getId()))
            .addOnFailureListener(e -> 
                System.err.println("Error adding document: " + e));
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
                        "Food Item : Bread\nExpires : Nov 29\nQuantity : 5 loaves remaining\nPickup Time : Available by 5 pm",
                        "6.7 km away",
                        R.drawable.bread
                    ));
                    // Add other sample items...
                }
            });
    }
} 