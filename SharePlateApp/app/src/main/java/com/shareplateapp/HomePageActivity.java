package com.shareplateapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomePageActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView searchIcon, menuIcon;
    private GridLayout donationGrid;
    private BottomNavigationView bottomNavigationView;
    private EditText searchEditText;
    private List<DonationItem> allDonationItems = new ArrayList<>(); // Store all items

    // Sample data for donation items
    private List<DonationItem> donationItems = new ArrayList<>();

    // Add these fields at the top of the class
    private LinearLayout searchLayout;
    private ImageView backArrow;
    private View normalToolbarContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Get references to views
        toolbar = findViewById(R.id.toolbar);
        searchIcon = findViewById(R.id.search_icon);
        menuIcon = findViewById(R.id.menu_icon);
        donationGrid = findViewById(R.id.donation_grid);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up the toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        // Set up search functionality
        searchIcon.setOnClickListener(v -> {
            // Show search layout
            searchLayout.setVisibility(View.VISIBLE);
            normalToolbarContent.setVisibility(View.GONE);
            searchEditText.requestFocus();
            
            // Show keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
        });

        // Initialize search EditText
        searchEditText = findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterDonationItems(s.toString());
            }
        });

        // Set up bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                // Already on the home page, do nothing
            } else if (item.getItemId() == R.id.navigation_community) {
                // TODO: Navigate to Community screen
            } else if (item.getItemId() == R.id.navigation_actions) {
                // TODO: Navigate to Actions screen
            } else if (item.getItemId() == R.id.navigation_profile) {
                // TODO: Navigate to Profile screen
            }
            return true;
        });

        // Sample data (replace with your data loading logic)
        donationItems.add(new DonationItem("Fresh Bread", "Food Item : Bread\nExpires : Nov 29\nQuantity : 5 loaves remaining\nPickup Time : Available by 5 pm", "6.7 km away", R.drawable.bread));
        donationItems.add(new DonationItem("Leftover Pizza", "Food Item: Pizza\nExpires: Nov 28\nQuantity: 2 slices remaining\nPickup Time: *Pick up by 2 PM", "1.8 km away", R.drawable.pizza));
        donationItems.add(new DonationItem("Non-dairy Milk", "Food Item : Non-dairy milk\nExpires : Dec 5\nQuantity : 7 cartons remaining\nPickup Time : Available by 6 pm", "6.7 km away", R.drawable.milk));
        donationItems.add(new DonationItem("Fruit Basket", "Food Item : Fruits\nExpires : Feb 4\nQuantity : 8 baskets remaining\nPickup Time : Available anytime", "7 km away", R.drawable.fruits));
        donationItems.add(new DonationItem("Bags", "Item : Backpacks\nCondition : Used\nCategory Tag : Accessories\nPickup Time : Available by 7 pm", "13.8 km away", R.drawable.bags));
        donationItems.add(new DonationItem("Pasta", "Food Item : Packaged Pasta\nExpires : Jun 21\nQuantity : 10 packs remaining\nPickup Time : Available anytime", "9.5 km away", R.drawable.pasta));
        donationItems.add(new DonationItem("Cooked Rice", "Food Item : Rice\nExpires : Jan 13\nQuantity : 6 servings remaining\nPickup Time : Available by 4 pm", "29.7 km away", R.drawable.rice));
        donationItems.add(new DonationItem("Clothing", "Item : Shirts\nCondition : Gently used\nCategory Tag : Clothing\nPickup Time : Available by 9 am", "10 km away", R.drawable.clothing));

        // Store all donation items
        allDonationItems.addAll(donationItems);

        // Inflate and add donation item views to the grid
        for (DonationItem item : donationItems) {
            addDonationItemView(item);
        }

        // In onCreate, after finding views:
        searchLayout = findViewById(R.id.search_layout);
        backArrow = findViewById(R.id.back_arrow);
        normalToolbarContent = findViewById(R.id.normal_toolbar_content);

        // Add back arrow click listener
        backArrow.setOnClickListener(v -> {
            // Hide search layout
            searchLayout.setVisibility(View.GONE);
            normalToolbarContent.setVisibility(View.VISIBLE);
            searchEditText.setText("");
            
            // Hide keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        });
    }

    private void addDonationItemView(DonationItem item) {
        View itemView = getLayoutInflater().inflate(R.layout.donation_item_view, donationGrid, false);

        ImageView itemImage = itemView.findViewById(R.id.item_image);
        ImageView bookmarkIcon = itemView.findViewById(R.id.bookmark_icon);
        TextView itemName = itemView.findViewById(R.id.item_name);
        TextView itemDescription = itemView.findViewById(R.id.item_description);
        TextView itemDistance = itemView.findViewById(R.id.item_distance);

        itemImage.setImageResource(item.getImageResourceId());
        itemName.setText(item.getName());
        itemDescription.setText(item.getDescription());
        itemDistance.setText(item.getDistance());

        // Set click listener for each item
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Handle item click (e.g., open item details)
            }
        });

        donationGrid.addView(itemView);
    }

    private void filterDonationItems(String query) {
        donationGrid.removeAllViews(); // Clear current items
        
        if (query.isEmpty()) {
            // Show all items if search is empty
            for (DonationItem item : allDonationItems) {
                addDonationItemView(item);
            }
        } else {
            // Filter items based on name or description
            String lowercaseQuery = query.toLowerCase();
            List<DonationItem> filteredItems = allDonationItems.stream()
                .filter(item -> 
                    item.getName().toLowerCase().contains(lowercaseQuery) ||
                    item.getDescription().toLowerCase().contains(lowercaseQuery))
                .collect(Collectors.toList());
            
            for (DonationItem item : filteredItems) {
                addDonationItemView(item);
            }
        }
    }

    // Placeholder for donation item data
    public static class DonationItem {
        private String name;
        private String description;
        private String distance;
        private int imageResourceId;

        public DonationItem(String name, String description, String distance, int imageResourceId) {
            this.name = name;
            this.description = description;
            this.distance = distance;
            this.imageResourceId = imageResourceId;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getDistance() {
            return distance;
        }

        public int getImageResourceId() {
            return imageResourceId;
        }
    }
}