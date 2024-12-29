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

public class FoodItemDetailFragment extends Fragment {
    private static final String ARG_FOOD_ITEM = "food_item";
    
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

        // Add back button click listener
        ImageView backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Get views
        ImageView itemImage = view.findViewById(R.id.detail_item_image);
        TextView itemName = view.findViewById(R.id.detail_item_name);
        TextView itemFoodCategory = view.findViewById(R.id.detail_item_foodCategory);
        TextView itemExpiredDate = view.findViewById(R.id.detail_item_expiredDate);
        TextView itemQuantity = view.findViewById(R.id.detail_item_quantity);
        TextView itemPickupTime = view.findViewById(R.id.detail_item_pickupTime);
        TextView itemLocation = view.findViewById(R.id.detail_item_location); // Make sure this ID exists in your layout
        TextView itemOwner = view.findViewById(R.id.detail_item_owner);
        TextView itemStatus = view.findViewById(R.id.detail_item_status);
        TextView itemCreatedAt = view.findViewById(R.id.detail_item_created_at);

        // Add click listener to the image
        itemImage.setOnClickListener(v -> {
            DonationItem item = (DonationItem) getArguments().getSerializable(ARG_FOOD_ITEM);
            if (item != null) {
                FullScreenImageFragment fullScreenFragment = 
                    FullScreenImageFragment.newInstance(item.getImageUrl(), item.getImageResourceId());
                requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fullScreenFragment)
                    .addToBackStack(null)
                    .commit();
            }
        });

        ImageView ownerProfileImage = view.findViewById(R.id.owner_profile_image);
        
        // Make the profile image circular
        ownerProfileImage.setClipToOutline(true);
        ownerProfileImage.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });

        // Get the DonationItem from arguments
        if (getArguments() != null) {
            DonationItem item = (DonationItem) getArguments().getSerializable(ARG_FOOD_ITEM);
            if (item != null) {
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
                itemFoodCategory.setText("Food Category: " + (item.getFoodCategory() != null ? item.getFoodCategory() : "N/A"));
                itemExpiredDate.setText("Expires: " + (item.getExpiredDate() != null ? item.getExpiredDate() : "N/A"));
                itemQuantity.setText("Quantity: " + (item.getQuantity() != null ? item.getQuantity() : "N/A"));
                itemPickupTime.setText("Pickup Time: " + (item.getPickupTime() != null ? item.getPickupTime() : "N/A"));
                itemLocation.setText("Location: " + (item.getLocation() != null ? item.getLocation() : "N/A"));
                itemOwner.setText(item.getOwnerUsername() != null ? 
                    item.getOwnerUsername() : "Anonymous");

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
            }
        }

        Button deleteButton = view.findViewById(R.id.deleteButton);
        Button requestButton = view.findViewById(R.id.requestButton);
        Button completeButton = view.findViewById(R.id.completeButton);
        
        if (getArguments() != null) {
            DonationItem item = (DonationItem) getArguments().getSerializable(ARG_FOOD_ITEM);
            if (item != null) {
                // Check if current user is the owner
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String currentUsername = currentUser != null ? currentUser.getDisplayName() : null;
                
                if (currentUsername != null && currentUsername.equals(item.getOwnerUsername())) {
                    // Show owner controls
                    deleteButton.setVisibility(View.VISIBLE);
                    requestButton.setVisibility(View.GONE);
                    
                    // Show complete button only if donation is active
                    if ("active".equals(item.getStatus())) {
                        completeButton.setVisibility(View.VISIBLE);
                        completeButton.setOnClickListener(v -> showCompleteConfirmation(item));
                    } else {
                        completeButton.setVisibility(View.GONE);
                    }
                    
                    deleteButton.setOnClickListener(v -> showDeleteConfirmation(item));
                } else {
                    // Non-owner view
                    deleteButton.setVisibility(View.GONE);
                    completeButton.setVisibility(View.GONE);
                    // Only show request button if donation is active
                    requestButton.setVisibility("active".equals(item.getStatus()) ? 
                        View.VISIBLE : View.GONE);
                }
            }
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
} 