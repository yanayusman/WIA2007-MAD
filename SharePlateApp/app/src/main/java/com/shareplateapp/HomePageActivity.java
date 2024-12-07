package com.shareplateapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView searchIcon, menuIcon;
    private LinearLayout donationItemsContainer;
    private BottomNavigationView bottomNavigationView;

    // Sample data for donation items (replace with your actual data)
    private List<DonationItem> donationItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Get references to views
        toolbar = findViewById(R.id.toolbar);
        searchIcon = findViewById(R.id.search_icon);
        menuIcon = findViewById(R.id.menu_icon);
        donationItemsContainer = findViewById(R.id.donation_items_container);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up the toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        // Set up bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    // Already on the home page, do nothing
                    break;
                case R.id.navigation_community:
                    // TODO: Navigate to Community screen
                    break;
                case R.id.navigation_discover:
                    // TODO: Navigate to Discover screen
                    break;
                case R.id.navigation_profile:
                    // TODO: Navigate to Profile screen
                    break;
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
        donationItems.add(new DonationItem("Books", "Item : Novels\nCondition : Good condition\nCategory Tag : Books\nPickup Time : Available by 12 pm", "19 km away", R.drawable.books));

        // Inflate and add donation item views
        for (DonationItem item : donationItems) {
            addDonationItemView(item);
        }
    }

    private void addDonationItemView(DonationItem item) {
        View itemView = getLayoutInflater().inflate(R.layout.donation_item_view, donationItemsContainer, false);

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

        donationItemsContainer.addView(itemView);
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