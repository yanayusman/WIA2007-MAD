package com.shareplateapp;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventRepo {
    private static final String COLLECTION_NAME = "events";
    private final FirebaseFirestore db;

    public EventRepo() {
        this.db = FirebaseFirestore.getInstance();
    }

    public interface OnEventItemsLoadedListener {
        void onEventItemsLoaded(List<Event> events);
        void onError(Exception e);
    }

    public void getAllEventItems(OnEventItemsLoadedListener listener) {
        db.collection(COLLECTION_NAME)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> items = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String name = document.getString("name");
                        String desc = document.getString("description");
                        String date = document.getString("date");
                        String time = document.getString("time");
                        String typeOfEvents = document.getString("typeOfEvents");
                        String seatAvailable = document.getString("seatsAvailable");
                        String location = document.getString("location");
                        Long imageResourceID = document.getLong("imageResourceID");
                        int img = imageResourceID != null ? imageResourceID.intValue() : 0;
                        String imageUrl = document.getString("imageUrl");
                        String ownerImageUrl = document.getString("ownerImageUrl");

                        items.add(new Event(name, desc, date, time, typeOfEvents, seatAvailable, location, img, imageUrl, ownerImageUrl));
                    }
                    listener.onEventItemsLoaded(items);
                })
                .addOnFailureListener(listener::onError);
    }

}