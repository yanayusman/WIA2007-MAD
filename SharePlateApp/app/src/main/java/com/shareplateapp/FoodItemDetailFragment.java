package com.shareplateapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

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
            }
        }
    }
} 