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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {

    private Toolbar toolbar;
    private ImageView searchIcon, menuIcon;
    private GridLayout donationGrid;
    private EditText searchEditText;
    private List<DonationItem> allDonationItems = new ArrayList<>();

    private LinearLayout searchLayout;
    private ImageView backArrow;
    private View normalToolbarContent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize all views first
        toolbar = view.findViewById(R.id.toolbar);
        searchIcon = view.findViewById(R.id.search_icon);
        menuIcon = view.findViewById(R.id.menu_icon);
        donationGrid = view.findViewById(R.id.donation_grid);
        searchEditText = view.findViewById(R.id.search_edit_text);
        searchLayout = view.findViewById(R.id.search_layout);
        backArrow = view.findViewById(R.id.back_arrow);
        normalToolbarContent = view.findViewById(R.id.normal_toolbar_content);

        // Initialize donation items list
        allDonationItems = new ArrayList<>();

        // Add sample data (move this to a separate method if you fetch from a database/API)
        allDonationItems.add(new DonationItem("Fresh Bread", "Food Item : Bread\nExpires : Nov 29\nQuantity : 5 loaves remaining\nPickup Time : Available by 5 pm", "6.7 km away", R.drawable.bread));
        allDonationItems.add(new DonationItem("Leftover Pizza", "Food Item: Pizza\nExpires: Nov 28\nQuantity: 2 slices remaining\nPickup Time: *Pick up by 2 PM", "1.8 km away", R.drawable.pizza));
        allDonationItems.add(new DonationItem("Non-dairy Milk", "Food Item : Non-dairy milk\nExpires : Dec 5\nQuantity : 7 cartons remaining\nPickup Time : Available by 6 pm", "6.7 km away", R.drawable.milk));
        allDonationItems.add(new DonationItem("Fruit Basket", "Food Item : Fruits\nExpires : Feb 4\nQuantity : 8 baskets remaining\nPickup Time : Available anytime", "7 km away", R.drawable.fruits));
        allDonationItems.add(new DonationItem("Bags", "Item : Backpacks\nCondition : Used\nCategory Tag : Accessories\nPickup Time : Available by 7 pm", "13.8 km away", R.drawable.bags));
        allDonationItems.add(new DonationItem("Pasta", "Food Item : Packaged Pasta\nExpires : Jun 21\nQuantity : 10 packs remaining\nPickup Time : Available anytime", "9.5 km away", R.drawable.pasta));
        allDonationItems.add(new DonationItem("Cooked Rice", "Food Item : Rice\nExpires : Jan 13\nQuantity : 6 servings remaining\nPickup Time : Available by 4 pm", "29.7 km away", R.drawable.rice));
        allDonationItems.add(new DonationItem("Clothing", "Item : Shirts\nCondition : Gently used\nCategory Tag : Clothing\nPickup Time : Available by 9 am", "10 km away", R.drawable.clothing));

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
                filterDonationItems(s.toString());
            }
        });

        // Inflate and add donation item views to the grid
        for (DonationItem item : allDonationItems) {
            addDonationItemView(item);
        }

        // Urgent Donation Card components
        CardView urgentDonationCard = view.findViewById(R.id.urgent_donation_card);
        TextView urgentText = view.findViewById(R.id.urgent_text);
        TextView itemTitle = view.findViewById(R.id.item_title);
        TextView pickupText = view.findViewById(R.id.pickup_text);
        ImageView itemImage = view.findViewById(R.id.item_image);
        TextView foodItemText = view.findViewById(R.id.food_item_text);
        TextView expiresText = view.findViewById(R.id.expires_text);
        TextView quantityText = view.findViewById(R.id.quantity_text);
        TextView pickupTimeText = view.findViewById(R.id.pickup_time_text);
        TextView perishableText = view.findViewById(R.id.perishable_text);

        return view;
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

        itemView.setOnClickListener(v -> {
            // TODO: Handle item click (e.g., open item details in a new fragment/activity)
        });

        donationGrid.addView(itemView);
    }

    private void filterDonationItems(String query) {
        donationGrid.removeAllViews();

        if (query.isEmpty()) {
            for (DonationItem item : allDonationItems) {
                addDonationItemView(item);
            }
        } else {
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
}