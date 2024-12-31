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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NonFoodItemDetail extends Fragment {
    private static final String ARG_ITEM = "non_food_item";

    private BroadcastReceiver profileUpdateReceiver;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NonFoodItem currentDonationItem;
    private RecyclerView detailRecyclerView;

    public static NonFoodItemDetail newInstance(DonationItem item) {
        NonFoodItemDetail fragment = new NonFoodItemDetail();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_non_food_item_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the NonFoodItem from arguments once
        if (getArguments() != null) {
            currentDonationItem = (NonFoodItem) getArguments().getSerializable(ARG_ITEM);
        }
        if (currentDonationItem == null) {
            return;
        }

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.button_green,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        swipeRefreshLayout.setOnRefreshListener(this::refreshItemDetails);

        // Set up back button in toolbar
        ImageView backButton = view.findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                requireActivity().getSupportFragmentManager().popBackStack();
            });
        }

        // Initialize RecyclerView
        detailRecyclerView = view.findViewById(R.id.detail_recycler_view);
        detailRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Convert your existing layout content into a RecyclerView item
        View contentView = LayoutInflater.from(requireContext()).inflate(R.layout.non_food_item_detail_content, null);

        // Setup views and load data
        setupViews(contentView);

        // Create a simple adapter with single item
        RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.non_food_item_detail_content, parent, false);
                return new RecyclerView.ViewHolder(itemView) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                setupViews(holder.itemView);
            }

            @Override
            public int getItemCount() {
                return 1;
            }
        };

        detailRecyclerView.setAdapter(adapter);
    }

    private void setupViews(View view) {
        // Remove the back button setup since it's now handled in onViewCreated
        // Get views and set their values
        updateUIWithDonationItem(view, currentDonationItem);

        Button editButton = view.findViewById(R.id.editButton);

        // Show edit button only for the owner
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentDonationItem != null &&
                currentDonationItem.getOwnerUsername().equals(currentUser.getDisplayName())) {
            editButton.setVisibility(View.VISIBLE);
            editButton.setOnClickListener(v -> openEditFragment());
        }
    }

    private void refreshItemDetails() {
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
                            NonFoodItem refreshedItem = documentSnapshot.toObject(NonFoodItem.class);
                            if (refreshedItem != null) {
                                refreshedItem.setDocumentId(documentSnapshot.getId());
                                currentDonationItem = refreshedItem;

                                // Update UI with fresh data
                                if (getView() != null) {
                                    updateUIWithDonationItem(getView(), refreshedItem);
                                }
                            }
                        } catch (Exception e) {
                            Log.e("ItemDetail", "Error refreshing data", e);
                            Toast.makeText(getContext(),
                                    "Error refreshing data: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    swipeRefreshLayout.setRefreshing(false);
                })
                .addOnFailureListener(e -> {
                    Log.e("ItemDetail", "Failed to refresh", e);
                    Toast.makeText(getContext(),
                            "Failed to refresh: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                });
    }

    private void updateUIWithDonationItem(View view, NonFoodItem item) {
        ImageView itemImage = view.findViewById(R.id.detail_item_image);
        TextView itemName = view.findViewById(R.id.detail_item_name);
        TextView itemCategory = view.findViewById(R.id.detail_item_category);
        TextView itemDescription = view.findViewById(R.id.detail_item_expired_date);
        TextView itemQuantity = view.findViewById(R.id.detail_item_quantity);
        TextView itemPickupTime = view.findViewById(R.id.detail_item_pickup_time);
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
        itemCategory.setText("Item Category : " + (item.getCategory() != null ? item.getCategory() : "N/A"));
        itemDescription.setText("Description : " + (item.getDescription() != null ? item.getDescription() : "N/A"));
        itemQuantity.setText("Quantity : " + (item.getQuantity() != null ? item.getQuantity() : "N/A"));
        itemPickupTime.setText("Pickup Time : " + (item.getPickupTime() != null ? item.getPickupTime() : "N/A"));
        itemLocation.setText("Location : " + (item.getLocation() != null ? item.getLocation() : "N/A"));
        itemOwner.setText(item.getOwnerUsername() != null ? item.getOwnerUsername() : "Anonymous");

        // Show status if completed
        if ("completed".equals(item.getStatus())) {
            itemStatus.setVisibility(View.VISIBLE);
            itemStatus.setText("Status : Completed");
            itemStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            itemStatus.setVisibility(View.GONE);
        }

        // Show creation date
        itemCreatedAt.setText("Posted on " + item.getFormattedCreationDate());

        // Update buttons visibility based on ownership and status
        updateButtonsVisibility(view, item);
    }

    private void updateButtonsVisibility(View view, NonFoodItem item) {
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
                NonFoodItem currentItem = getArguments() != null ?
                        (NonFoodItem) getArguments().getSerializable(ARG_ITEM) : null;

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

    private void showDeleteConfirmation(NonFoodItem item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Donation")
                .setMessage("Are you sure you want to delete this donation?")
                .setPositiveButton("Delete", (dialog, which) -> deleteDonationItem(item))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteDonationItem(NonFoodItem item) {
        if (item.getDocumentId() == null) {
            Toast.makeText(getContext(), "Error: Cannot delete item without document ID",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        System.out.println("Deleting item with document ID: " + item.getDocumentId());

        NonFoodItemRepository repository = new NonFoodItemRepository();
        repository.deleteNonFoodItem(item.getDocumentId(), new NonFoodItemRepository.OnDeleteCompleteListener() {
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

    private void showCompleteConfirmation(NonFoodItem item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Complete Donation")
                .setMessage("Mark this donation as completed?")
                .setPositiveButton("Complete", (dialog, which) -> completeDonation(item))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void completeDonation(NonFoodItem item) {
        if (item.getDocumentId() == null) {
            Toast.makeText(getContext(), "Error: Cannot update item without document ID",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        NonFoodItemRepository repository = new NonFoodItemRepository();
        repository.updateDonationStatus(item.getDocumentId(), "completed",
                new NonFoodItemRepository.OnStatusUpdateListener() {
                    @Override
                    public void onUpdateSuccess() {
                        if (getContext() != null) {
                            // Update the UI to show completed status
                            TextView itemStatus = getView().findViewById(R.id.detail_item_status);
                            itemStatus.setVisibility(View.VISIBLE);
                            itemStatus.setText("Status : Completed");
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
            NonFoodItem donationItem = (NonFoodItem) getArguments().getSerializable(ARG_ITEM);
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

    private void openEditFragment() {
        EditNonFoodFragment editFragment = EditNonFoodFragment.newInstance(currentDonationItem);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, editFragment)
                .addToBackStack(null)
                .commit();
    }
}