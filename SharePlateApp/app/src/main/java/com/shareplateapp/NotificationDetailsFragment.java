package com.shareplateapp;

// This fragment displays detailed information for a single notification when the user taps on it.
// It shows more comprehensive data about the notification, such as additional information or
// context that wasn't included in the summary view on the NotificationFragment.

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NotificationDetailsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_details, container, false);

        ImageView backButton = view.findViewById(R.id.backButton);
        TextView title = view.findViewById(R.id.notificationTitle);
        TextView timestamp = view.findViewById(R.id.notificationTimestamp);
        TextView message = view.findViewById(R.id.notificationMessage);
        ImageView image = view.findViewById(R.id.notificationImage);
        TextView details = view.findViewById(R.id.notificationDetails);
        Button actionButton = view.findViewById(R.id.actionButton);

        // Replace these with data from a Notification object or database
        title.setText("Food Donation Alert");
        timestamp.setText("24 Dec 2024 at 4:44 PM");
        message.setText("Fresh vegetables are available for pickup!");
        details.setText("A local grocery store has donated 10 kg of fresh vegetables. 🥬🍅\n\nLocation: Taman Midah Community Center\n\nAvailable until 6 PM today. Act fast!");
        image.setImageResource(R.drawable.sample_food_image); // Replace with dynamic image URL if applicable

        // Back button action
        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        // Action button to view on map
        actionButton.setOnClickListener(v -> {
            // Pass location coordinates or URL to map activity
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("geo:0,0?q=Taman Midah Community Center"));
            if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                startActivity(intent);
            }
        });

        return view;
    }
}
}