package com.shareplateapp;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommunityAllFragment extends Fragment {

    private Toolbar toolbar;
    private ImageView searchIcon, menuIcon;
    private LinearLayout eventGrid;
    private EditText searchEditText;
    private List<Event> allEvents = new ArrayList<>();
    private LinearLayout searchLayout;
    private ImageView backArrow;
    private View normalToolbarContent;
    private EventRepo eventRepo;
    private List<Event> volunteeringEvents = new ArrayList<>();
    private List<Event> campaignEvents = new ArrayList<>();

    private Chip allEventsButton, volunteeringButton, campaignsButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_all, container, false);

        eventRepo = new EventRepo();
        fetchEventsFromDatabase();

        // Initialize views
        toolbar = view.findViewById(R.id.toolbar2);
        searchIcon = view.findViewById(R.id.search_icon2);
        menuIcon = view.findViewById(R.id.menu_icon2);
        allEventsButton = view.findViewById(R.id.allEventsButton);
        volunteeringButton = view.findViewById(R.id.volunteeringButton);
        campaignsButton = view.findViewById(R.id.campaignsButton);
        eventGrid = view.findViewById(R.id.event_grid);
        searchEditText = view.findViewById(R.id.search_edit_text2);
        searchLayout = view.findViewById(R.id.search_layout2);
        backArrow = view.findViewById(R.id.back_arrow2);
        normalToolbarContent = view.findViewById(R.id.normal_toolbar_content2);


        // Set up search functionality
        searchIcon.setOnClickListener(v -> {
            searchLayout.setVisibility(View.VISIBLE);
            normalToolbarContent.setVisibility(View.GONE);
            searchEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        backArrow.setOnClickListener(v -> {
            searchLayout.setVisibility(View.GONE);
            normalToolbarContent.setVisibility(View.VISIBLE);
            searchEditText.setText("");
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterEvents(s.toString());
            }
        });

        // Initialize views
        allEventsButton.setOnClickListener(v -> navigateToFragment(new CommunityAllFragment()));
        volunteeringButton.setOnClickListener(v -> navigateToFragment(new CommunityVolunteeringFragment()));
        campaignsButton.setOnClickListener(v -> navigateToFragment(new CommunityCampaignsFragment()));
        return view;
    }

    private void showEvents(List<Event> events) {
        // Clear existing views
        eventGrid.removeAllViews();

        // Add events to the grid
        for (Event event : events) {
            addEventsView(event);
        }
    }

    private void navigateToFragment(Fragment fragment) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void fetchEventsFromDatabase() {
        eventRepo.getAllEventItems(new EventRepo.OnEventItemsLoadedListener() {
            @Override
            public void onEventItemsLoaded(List<Event> items) {
                allEvents = items; // Update the event list
                eventGrid.removeAllViews(); // Clear existing views
                for (Event event : allEvents) {
                    addEventsView(event); // Add events to the grid
                }
            }
            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Error loading events: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addEventsView(Event event) {
        View eventItem = getLayoutInflater().inflate(R.layout.event_item_view, eventGrid, false);

        ImageView eventImg = eventItem.findViewById(R.id.item_image);
        TextView eventName = eventItem.findViewById(R.id.item_name);
        TextView eventDate = eventItem.findViewById(R.id.item_date);
        TextView eventLocation = eventItem.findViewById(R.id.item_location);
        TextView eventDesc = eventItem.findViewById(R.id.item_desc);

        eventImg.setImageResource(event.getImageResourceId());
        eventName.setText(event.getName());
        eventDate.setText("Date : " + (event.getDate() != null ? event.getDate() : "N/A"));
        eventLocation.setText("Location : " + (event.getLocation() != null ? event.getLocation() : "N/A"));
        eventDesc.setText("Description : " + (event.getDescription() != null ? event.getDescription() : "N/A"));

        eventGrid.addView(eventItem);
    }

    private void filterEvents(String query) {
        eventGrid.removeAllViews();

        if (query.isEmpty()) {
            for (Event event : allEvents) {
                addEventsView(event);
            }
        } else {
            String lowercaseQuery = query.toLowerCase();
            List<Event> filteredEvents = allEvents.stream()
                    .filter(item ->
                            item.getName().toLowerCase().contains(lowercaseQuery) ||
                                    item.getDate().toLowerCase().contains(lowercaseQuery) ||
                                    item.getLocation().toLowerCase().contains(lowercaseQuery) ||
                                    item.getDescription().toLowerCase().contains(lowercaseQuery))
                    .collect(Collectors.toList());

            for (Event event : filteredEvents) {
                addEventsView(event);
            }
        }
    }
}