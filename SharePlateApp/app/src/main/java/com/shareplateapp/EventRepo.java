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

                        items.add(new Event(name, desc, date, time, typeOfEvents, seatAvailable, location, img));
                    }
                    listener.onEventItemsLoaded(items);
                })
                .addOnFailureListener(listener::onError);
    }

    public void addEventItem(Event item) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("name", item.getName());
        eventData.put("description", item.getDescription());
        eventData.put("date", item.getDate());
        eventData.put("time", item.getTime());
        eventData.put("location", item.getLocation());
        eventData.put("imageResourceID", item.getImageResourceId());

        db.collection(COLLECTION_NAME)
                .add(eventData)
                .addOnSuccessListener(documentReference ->
                        System.out.println("Event added with ID: " + documentReference.getId()))
                .addOnFailureListener(e ->
                        System.err.println("Error adding event: " + e));
    }
}