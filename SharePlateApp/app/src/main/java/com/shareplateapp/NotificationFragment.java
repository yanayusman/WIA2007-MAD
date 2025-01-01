package com.shareplateapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// This fragment displays the list of all notifications in the app.
// It handles the display of all notifications by using a RecyclerView to show each notification item.

public class NotificationFragment extends Fragment {

    private RecyclerView notificationRecyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        notificationRecyclerView = view.findViewById(R.id.notificationRecyclerView);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        notificationList = new ArrayList<>();

        // Sample notifications related to food donations
        notificationList.add(new Notification("New Donation", "A user just donated a bag of rice to your local food pantry.", "15 mins ago", R.drawable.ic_food_donation));
        notificationList.add(new Notification("Food Request Fulfilled", "Your request for fruits has been fulfilled by a community member.", "2 hours ago", R.drawable.ic_food_request));
        notificationList.add(new Notification("Upcoming Event", "Join our community food drive this Saturday at the park!", "1 day ago", R.drawable.ic_food_drive));
        notificationList.add(new Notification("Donation Received", "You have received a new donation of vegetables.", "3 hours ago", R.drawable.ic_food_donation));

        notificationAdapter = new NotificationAdapter(notificationList, notification -> {
            // Handle notification click
        });
        notificationRecyclerView.setAdapter(notificationAdapter);

        return view;
    }
}
