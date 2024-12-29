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
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.text.SimpleDateFormat;

import com.bumptech.glide.Glide;

public class HomeFragment extends Fragment {

    private Toolbar toolbar;
    private ImageView searchIcon, menuIcon;
    private GridLayout donationGrid;
    private EditText searchEditText;
    private List<DonationItem> allDonationItems = new ArrayList<>();

    private LinearLayout searchLayout;
    private ImageView backArrow;
    private View normalToolbarContent;

    private DonationItemRepository donationItemRepository;

    private SwipeRefreshLayout swipeRefreshLayout;

    private SortOption currentSortOption = SortOption.DEFAULT;
    private SortDirection currentSortDirection = SortDirection.ASCENDING;

    private ImageView sortIcon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views and repository
        initializeViews(view);
        donationItemRepository = new DonationItemRepository();

        // Set up swipe refresh
        swipeRefreshLayout.setOnRefreshListener(this::loadDonationItems);
        
        // Load donation items from Firestore
        loadDonationItems();

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

        Button sortButton = view.findViewById(R.id.sortButton);
        sortButton.setOnClickListener(v -> showSortOptions());

        /* Comment out Urgent Donation Card components
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
        */

        return view;
    }

    private void initializeViews(View view) {
        // Initialize all views first
        toolbar = view.findViewById(R.id.toolbar);
        searchIcon = view.findViewById(R.id.search_icon);
//        menuIcon = view.findViewById(R.id.menu_icon);
        donationGrid = view.findViewById(R.id.donation_grid);
        searchEditText = view.findViewById(R.id.search_edit_text);
        searchLayout = view.findViewById(R.id.search_layout);
        backArrow = view.findViewById(R.id.back_arrow);
        normalToolbarContent = view.findViewById(R.id.normal_toolbar_content);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        sortIcon = view.findViewById(R.id.sort_icon);
        
        // Set up sort functionality
        sortIcon.setOnClickListener(v -> showSortOptions());
    }

    private void loadDonationItems() {
        // Show refresh indicator
        swipeRefreshLayout.setRefreshing(true);
        
        donationItemRepository.getAllDonationItems(new DonationItemRepository.OnDonationItemsLoadedListener() {
            @Override
            public void onDonationItemsLoaded(List<DonationItem> items) {
                allDonationItems = items;
                // Clear existing views
                donationGrid.removeAllViews();
                // Add loaded items to the grid
                for (DonationItem item : allDonationItems) {
                    addDonationItemView(item);
                }
                // Hide refresh indicator
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Exception e) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), 
                        "Error loading donation items: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
                // Hide refresh indicator even on error
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void addDonationItemView(DonationItem item) {
        View itemView = getLayoutInflater().inflate(R.layout.donation_item_view, donationGrid, false);

        ImageView itemImage = itemView.findViewById(R.id.item_image);
        TextView itemName = itemView.findViewById(R.id.item_name);
        TextView itemFoodCategory = itemView.findViewById(R.id.item_foodCategory);
        TextView itemExpiredDate = itemView.findViewById(R.id.item_expiredDate);
        TextView itemQuantity = itemView.findViewById(R.id.item_quantity);
        TextView itemPickupTime = itemView.findViewById(R.id.item_pickupTime);
        TextView itemDistance = itemView.findViewById(R.id.item_distance);
        TextView statusIndicator = itemView.findViewById(R.id.status_indicator);

        // Show status indicator if item is completed
        if (item.getStatus() != null && item.getStatus().equals("completed")) {
            statusIndicator.setVisibility(View.VISIBLE);
            // Optional: Add some visual dimming to the entire card
            itemView.setAlpha(0.8f);
        } else {
            statusIndicator.setVisibility(View.GONE);
            itemView.setAlpha(1.0f);
        }

        // Load image using Glide
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(this)
                .load(item.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .centerCrop()
                .into(itemImage);
        } else {
            itemImage.setImageResource(item.getImageResourceId());
        }

        itemName.setText(item.getName());
        itemFoodCategory.setText(item.getFoodCategory());
        itemExpiredDate.setText(item.getExpiredDate());
        itemQuantity.setText(item.getQuantity());
        itemPickupTime.setText(item.getPickupTime());
        itemDistance.setText(item.getLocation());

        itemView.setOnClickListener(v -> {
            FoodItemDetailFragment detailFragment = FoodItemDetailFragment.newInstance(item);
            requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
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
                                    item.getFoodCategory().toLowerCase().contains(lowercaseQuery) ||
                                    item.getExpiredDate().toLowerCase().contains(lowercaseQuery) ||
                                    String.valueOf(item.getQuantity()).contains(lowercaseQuery) ||
                                    item.getPickupTime().toLowerCase().contains(lowercaseQuery))
                    .collect(Collectors.toList());

            for (DonationItem item : filteredItems) {
                addDonationItemView(item);
            }
        }
    }

    private void showSortOptions() {
        String[] options = Arrays.stream(SortOption.values())
            .map(SortOption::getDisplayName)
            .toArray(String[]::new);

        new AlertDialog.Builder(requireContext())
            .setTitle("Sort by")
            .setSingleChoiceItems(options, currentSortOption.ordinal(), (dialog, which) -> {
                currentSortOption = SortOption.values()[which];
                if (currentSortOption != SortOption.DEFAULT) {
                    showSortDirectionDialog();
                } else {
                    sortDonationItems();
                }
                dialog.dismiss();
            })
            .show();
    }

    private void showSortDirectionDialog() {
        String[] directions = Arrays.stream(SortDirection.values())
            .map(SortDirection::getDisplayName)
            .toArray(String[]::new);

        new AlertDialog.Builder(requireContext())
            .setTitle("Sort Direction")
            .setSingleChoiceItems(directions, currentSortDirection.ordinal(), (dialog, which) -> {
                currentSortDirection = SortDirection.values()[which];
                sortDonationItems();
                dialog.dismiss();
            })
            .show();
    }

    private void sortDonationItems() {
        if (allDonationItems == null) return;

        List<DonationItem> sortedItems = new ArrayList<>(allDonationItems);
        
        switch (currentSortOption) {
            case DATE_CREATED:
                // Assuming newer items are at the end of the list
                Collections.reverse(sortedItems);
                break;
                
            case EXPIRY_DATE:
                sortedItems.sort((a, b) -> {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                        Date dateA = sdf.parse(a.getExpiredDate());
                        Date dateB = sdf.parse(b.getExpiredDate());
                        return currentSortDirection == SortDirection.ASCENDING ? 
                            dateA.compareTo(dateB) : dateB.compareTo(dateA);
                    } catch (Exception e) {
                        return 0;
                    }
                });
                break;
                
            case CATEGORY:
                sortedItems.sort((a, b) -> {
                    int result = a.getFoodCategory().compareToIgnoreCase(b.getFoodCategory());
                    return currentSortDirection == SortDirection.ASCENDING ? result : -result;
                });
                break;
                
            case QUANTITY:
                sortedItems.sort((a, b) -> {
                    try {
                        int qtyA = Integer.parseInt(a.getQuantity().replaceAll("[^0-9]", ""));
                        int qtyB = Integer.parseInt(b.getQuantity().replaceAll("[^0-9]", ""));
                        return currentSortDirection == SortDirection.ASCENDING ? 
                            Integer.compare(qtyA, qtyB) : Integer.compare(qtyB, qtyA);
                    } catch (Exception e) {
                        return 0;
                    }
                });
                break;
                
            case LOCATION:
                sortedItems.sort((a, b) -> {
                    int result = a.getLocation().compareToIgnoreCase(b.getLocation());
                    return currentSortDirection == SortDirection.ASCENDING ? result : -result;
                });
                break;
                
            case PICKUP_TIME:
                sortedItems.sort((a, b) -> {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);
                        Date timeA = sdf.parse(a.getPickupTime());
                        Date timeB = sdf.parse(b.getPickupTime());
                        return currentSortDirection == SortDirection.ASCENDING ? 
                            timeA.compareTo(timeB) : timeB.compareTo(timeA);
                    } catch (Exception e) {
                        return 0;
                    }
                });
                break;
                
            case DEFAULT:
            default:
                // Do nothing, keep original order
                break;
        }

        // Clear and reload the grid with sorted items
        donationGrid.removeAllViews();
        for (DonationItem item : sortedItems) {
            addDonationItemView(item);
        }
    }

}