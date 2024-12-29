package com.shareplateapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewOutlineProvider;
import android.graphics.Outline;
import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;

public class FoodItemDetailFragment extends Fragment {
    private static final String ARG_FOOD_ITEM = "food_item";
    
    private BroadcastReceiver profileUpdateReceiver;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DonationItem currentDonationItem;

    public static FoodItemDetailFragment newInstance(DonationItem item) {
        FoodItemDetailFragment fragment = new FoodItemDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FOOD_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_food_item_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(
            R.color.button_green,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        );
        
        swipeRefreshLayout.setOnRefreshListener(this::refreshFoodDetails);

        // Get the DonationItem from arguments once
        if (getArguments() != null) {
            currentDonationItem = (DonationItem) getArguments().getSerializable(ARG_FOOD_ITEM);
        }
        if (currentDonationItem == null) {
            return;
        }

        // Setup views and load data
        setupViews(view);
    }

    private void setupViews(View view) {
        // Move all your view setup code here from onViewCreated
        // Add back button click listener
        ImageView backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Get views and set their values
        updateUIWithDonationItem(view, currentDonationItem);
    }

    private void refreshFoodDetails() {
        if (currentDonationItem == null || currentDonationItem.getDocumentId() == null) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        // Get fresh data from Firestore
        FirebaseFirestore.getInstance()
            .collection("allDonationItems")
            .document(currentDonationItem.getDocumentId())
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    try {
                        // Create new DonationItem from the fresh data
                        DonationItem refreshedItem = documentSnapshot.toObject(DonationItem.class);
                        if (refreshedItem != null) {
                            refreshedItem.setDocumentId(documentSnapshot.getId());
                            currentDonationItem = refreshedItem;
                            
                            // Update UI with fresh data
                            if (getView() != null) {
                                updateUIWithDonationItem(getView(), refreshedItem);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("FoodItemDetail", "Error refreshing data", e);
                        Toast.makeText(getContext(), 
                            "Error refreshing data: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
            })
            .addOnFailureListener(e -> {
                Log.e("FoodItemDetail", "Failed to refresh", e);
                Toast.makeText(getContext(), 
                    "Failed to refresh: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            });
    }

    private void updateUIWithDonationItem(View view, DonationItem item) {
        ImageView itemImage = view.findViewById(R.id.detail_item_image);
        TextView itemName = view.findViewById(R.id.detail_item_name);
        TextView itemFoodCategory = view.findViewById(R.id.detail_item_foodCategory);
        TextView itemExpiredDate = view.findViewById(R.id.detail_item_expiredDate);
        TextView itemQuantity = view.findViewById(R.id.detail_item_quantity);
        TextView itemPickupTime = view.findViewById(R.id.detail_item_pickupTime);
        TextView itemLocation = view.findViewById(R.id.detail_item_location);
        TextView itemOwner = view.findViewById(R.id.detail_item_owner);
        TextView itemStatus = view.findViewById(R.id.detail_item_status);
        TextView itemCreatedAt = view.findViewById(R.id.detail_item_created_at);
        ImageView ownerProfileImage = view.findViewById(R.id.owner_profile_image);

        // Add click listener to the image
        itemImage.setOnClickListener(v -> {
            FullScreenImageFragment fullScreenFragment = 
                FullScreenImageFragment.newInstance(item.getImageUrl(), item.getImageResourceId());
            requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fullScreenFragment)
                .addToBackStack(null)
                .commit();
        });

        // Set up owner profile image
        ownerProfileImage.setClipToOutline(true);
        ownerProfileImage.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });

        // Load the owner's profile image
        loadOwnerProfileImage(item.getOwnerUsername(), ownerProfileImage);

        // Set up owner profile image click listener
        ownerProfileImage.setOnClickListener(v -> {
            // Create and show FullScreenImageFragment with the owner's profile image
            FullScreenImageFragment fullScreenFragment = 
                FullScreenImageFragment.newInstance(item.getOwnerProfileImageUrl(), R.drawable.profile);
            requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fullScreenFragment)
                .addToBackStack(null)
                .commit();
        });

        // Load item image
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

        // Set text fields
        itemName.setText(item.getName());
        itemFoodCategory.setText("Food Category: " + (item.getFoodCategory() != null ? item.getFoodCategory() : "N/A"));
        itemExpiredDate.setText("Expires: " + (item.getExpiredDate() != null ? item.getExpiredDate() : "N/A"));
        itemQuantity.setText("Quantity: " + (item.getQuantity() != null ? item.getQuantity() : "N/A"));
        itemPickupTime.setText("Pickup Time: " + (item.getPickupTime() != null ? item.getPickupTime() : "N/A"));
        itemLocation.setText("Location: " + (item.getLocation() != null ? item.getLocation() : "N/A"));
        itemOwner.setText(item.getOwnerUsername() != null ? item.getOwnerUsername() : "Anonymous");

        // Show status if completed
        if ("completed".equals(item.getStatus())) {
            itemStatus.setVisibility(View.VISIBLE);
            itemStatus.setText("Status: Completed");
            itemStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            itemStatus.setVisibility(View.GONE);
        }

        // Show creation date
        itemCreatedAt.setText("Posted on " + item.getFormattedCreationDate());

        // Update buttons visibility based on ownership and status
        updateButtonsVisibility(view, item);
    }

    private void updateButtonsVisibility(View view, DonationItem item) {
        Button deleteButton = view.findViewById(R.id.deleteButton);
        Button requestButton = view.findViewById(R.id.requestButton);
        Button completeButton = view.findViewById(R.id.completeButton);
        
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUsername = currentUser != null ? currentUser.getDisplayName() : null;
        
        if (currentUsername != null && currentUsername.equals(item.getOwnerUsername())) {
            deleteButton.setVisibility(View.VISIBLE);
            requestButton.setVisibility(View.GONE);
            
            if ("active".equals(item.getStatus())) {
                completeButton.setVisibility(View.VISIBLE);
                completeButton.setOnClickListener(v -> showCompleteConfirmation(item));
            } else {
                completeButton.setVisibility(View.GONE);
            }
            
            deleteButton.setOnClickListener(v -> showDeleteConfirmation(item));
        } else {
            deleteButton.setVisibility(View.GONE);
            completeButton.setVisibility(View.GONE);
            requestButton.setVisibility("active".equals(item.getStatus()) ? 
                View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize the broadcast receiver
        profileUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String newProfileImageUrl = intent.getStringExtra("newProfileImageUrl");
                String ownerUsername = intent.getStringExtra("ownerUsername");
                
                // Get the current donation item
                DonationItem currentItem = getArguments() != null ? 
                    (DonationItem) getArguments().getSerializable(ARG_FOOD_ITEM) : null;
                    
                // Update the profile image if this detail view is for the updated user's donation
                if (currentItem != null && currentItem.getOwnerUsername().equals(ownerUsername)) {
                    ImageView ownerProfileImage = getView().findViewById(R.id.owner_profile_image);
                    if (getContext() != null && ownerProfileImage != null) {
                        Glide.with(getContext())
                            .load(newProfileImageUrl)
                            .circleCrop()
                            .placeholder(R.drawable.profile)
                            .error(R.drawable.profile)
                            .into(ownerProfileImage);
                    }
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register the broadcast receiver
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(profileUpdateReceiver, new IntentFilter("profile.image.updated"));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the broadcast receiver
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(profileUpdateReceiver);
        }
    }

    private void showDeleteConfirmation(DonationItem item) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Delete Donation")
            .setMessage("Are you sure you want to delete this donation?")
            .setPositiveButton("Delete", (dialog, which) -> deleteDonationItem(item))
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void deleteDonationItem(DonationItem item) {
        if (item.getDocumentId() == null) {
            Toast.makeText(getContext(), "Error: Cannot delete item without document ID", 
                Toast.LENGTH_SHORT).show();
            return;
        }

        System.out.println("Deleting item with document ID: " + item.getDocumentId());
        
        DonationItemRepository repository = new DonationItemRepository();
        repository.deleteDonationItem(item.getDocumentId(), new DonationItemRepository.OnDeleteCompleteListener() {
            @Override
            public void onDeleteSuccess() {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Donation deleted successfully", Toast.LENGTH_SHORT).show();
                    // Navigate back
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            }

            @Override
            public void onDeleteFailure(Exception e) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to delete donation: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showCompleteConfirmation(DonationItem item) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Complete Donation")
            .setMessage("Mark this donation as completed?")
            .setPositiveButton("Complete", (dialog, which) -> completeDonation(item))
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void completeDonation(DonationItem item) {
        if (item.getDocumentId() == null) {
            Toast.makeText(getContext(), "Error: Cannot update item without document ID", 
                Toast.LENGTH_SHORT).show();
            return;
        }

        DonationItemRepository repository = new DonationItemRepository();
        repository.updateDonationStatus(item.getDocumentId(), "completed", 
            new DonationItemRepository.OnStatusUpdateListener() {
                @Override
                public void onUpdateSuccess() {
                    if (getContext() != null) {
                        // Update the UI to show completed status
                        TextView itemStatus = getView().findViewById(R.id.detail_item_status);
                        itemStatus.setVisibility(View.VISIBLE);
                        itemStatus.setText("Status: Completed");
                        itemStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                        
                        // Hide the complete button
                        Button completeButton = getView().findViewById(R.id.completeButton);
                        completeButton.setVisibility(View.GONE);
                        
                        // Hide the request button
                        Button requestButton = getView().findViewById(R.id.requestButton);
                        requestButton.setVisibility(View.GONE);

                        Toast.makeText(getContext(), 
                            "Donation marked as complete", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onUpdateFailure(Exception e) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), 
                            "Failed to update donation: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void loadOwnerProfileImage(String ownerUsername, ImageView ownerProfileImage) {
        // Get the DonationItem from arguments
        if (getArguments() != null) {
            DonationItem donationItem = (DonationItem) getArguments().getSerializable(ARG_FOOD_ITEM);
            if (donationItem != null && donationItem.getOwnerProfileImageUrl() != null 
                && !donationItem.getOwnerProfileImageUrl().isEmpty()) {
                // Load the profile image using the stored URL
                if (getContext() != null) {
                    Glide.with(getContext())
                        .load(donationItem.getOwnerProfileImageUrl())
                        .circleCrop()
                        .placeholder(R.drawable.profile)
                        .error(R.drawable.profile)
                        .into(ownerProfileImage);
                }
            } else {
                // Load default image if no URL available
                if (getContext() != null) {
                    Glide.with(getContext())
                        .load(R.drawable.profile)
                        .circleCrop()
                        .into(ownerProfileImage);
                }
            }
        }
    }
} 