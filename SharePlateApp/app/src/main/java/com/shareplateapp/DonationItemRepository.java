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
                    String name = document.getString("name");
                    String foodCategory = document.getString("foodCategory");
                    String expiredDate = document.getString("expiredDate");
                    String quantity = document.getString("quantity");
                    String pickupTime = document.getString("pickupTime");
                    String location = document.getString("location");
                    int imageResourceId = document.getLong("imageResourceID").intValue();
                    
                    items.add(new DonationItem(name, foodCategory, expiredDate, quantity, pickupTime, location, imageResourceId));
                }
                listener.onDonationItemsLoaded(items);
            })
            .addOnFailureListener(listener::onError);
    }

    public void addDonationItem(DonationItem item) {
        Map<String, Object> donationData = new HashMap<>();
        donationData.put("name", item.getName());
        donationData.put("foodCategory", item.getFoodCategory());
        donationData.put("expiredDate", item.getFoodCategory());
        donationData.put("quantity", item.getQuantity());
        donationData.put("pickupTime", item.getPickupTime());
        donationData.put("location", item.getLocation());
        donationData.put("imageResourceID", item.getImageResourceId());

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
                        "Food Item : Bread",
                            "Expires : Nov 29",
                            "5",
                            "Pickup Time : Available by 5 pm",
                        "Petaling Jaya",
                        R.drawable.bread
                    ));
                }
            });
    }
} 