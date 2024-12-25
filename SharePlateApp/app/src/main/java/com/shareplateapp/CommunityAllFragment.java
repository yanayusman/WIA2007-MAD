package com.shareplateapp;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_community_all, container, false);

        // Initialize all views first
        toolbar = view.findViewById(R.id.toolbar2);
        searchIcon = view.findViewById(R.id.search_icon2);
        menuIcon = view.findViewById(R.id.menu_icon2);
        eventGrid = view.findViewById(R.id.event_grid);
        searchEditText = view.findViewById(R.id.search_edit_text2);
        searchLayout = view.findViewById(R.id.search_layout2);
        backArrow = view.findViewById(R.id.back_arrow2);
        normalToolbarContent = view.findViewById(R.id.normal_toolbar_content2);

        // Initialize donation items list
        allEvents = new ArrayList<>();

        // Add sample data (move this to a separate method if you fetch from a database/API)
        allEvents.add(new Campaign("Campaign for Zero Hunger", "Support SDG 2: Zero Hunger by attending this awareness event. Activities include food sharing, talks, and more.", "KLCC Park", "Dec 18, 2024", R.drawable.bread));
        allEvents.add(new Volunteering("Food Distribution Drive", "Help distribute surplus food to underprivileged families. Volunteers are needed for sorting, packaging, and handing out food items.", "Community Center, Jalan Semarak", "Dec 10, 2024", R.drawable.pizza, 16, 20));


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

        // Inflate and add donation item views to the grid
        for (Event item : allEvents) {
            addEventsView(item);
        }

        return view;
    }

    private void addEventsView(Event event){
        View itemView = getLayoutInflater().inflate(R.layout.event_item_view, eventGrid, false);

        ImageView itemImage = itemView.findViewById(R.id.item_image);
        TextView itemName = itemView.findViewById(R.id.item_name);
        TextView itemDescription = itemView.findViewById(R.id.item_description);
        TextView eventDate = itemView.findViewById(R.id.event_date);
        TextView eventLocation = itemView.findViewById(R.id.event_location);

        itemImage.setImageResource(event.getImageResourceId());
        itemName.setText(event.getTitle());
        itemDescription.setText(event.getDescription());
        eventDate.setText(event.getDate());
        eventLocation.setText(event.getLocation());

        eventGrid.addView(itemView);
    }

    private void filterEvents(String query){
        eventGrid.removeAllViews();

        if (query.isEmpty()) {
            for (Event event : allEvents) {
                addEventsView(event);
            }
        } else {
            String lowercaseQuery = query.toLowerCase();
            List<Event> filteredEvents = allEvents.stream()
                    .filter(item ->
                            item.getTitle().toLowerCase().contains(lowercaseQuery) ||
                                    item.getDescription().toLowerCase().contains(lowercaseQuery))
                    .collect(Collectors.toList());

            for (Event event : filteredEvents) {
                addEventsView(event);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // If you need to handle View clicks or other logic, do it here
        // Example (you'll need to adapt this to your actual logic):
        // Button interestedButton = view.findViewById(R.id.interestedButton);
        // interestedButton.setOnClickListener(v -> {
        //     // Handle click
        // });
    }
}