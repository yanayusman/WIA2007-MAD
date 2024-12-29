package com.shareplateapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.net.Uri;
import android.content.Intent;
import android.provider.MediaStore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.bumptech.glide.Glide;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.widget.ProgressBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DonateItemFragment extends Fragment {
    private EditText nameInput;
    private EditText foodCategoryInput;
    private EditText expiryDateInput;
    private EditText quantityInput;
    private EditText pickupTimeInput;
    private EditText locationInput;
    private Button submitButton;
    private ImageView backButton;
    private DonationItemRepository donationItemRepository;
    private Calendar calendar;
    private SimpleDateFormat dateFormatter;
    private Calendar timeCalendar;
    private SimpleDateFormat timeFormatter;
    private ImageView foodImageView;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        donationItemRepository = new DonationItemRepository();
        calendar = Calendar.getInstance();
        timeCalendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        timeFormatter = new SimpleDateFormat("hh:mm a", Locale.US); // 12-hour format with AM/PM
        
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    // Show selected image
                    Glide.with(this)
                        .load(selectedImageUri)
                        .centerCrop()
                        .into(foodImageView);
                }
            }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_donate_food, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        initializeViews(view);

        // Set up click listeners
        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        submitButton.setOnClickListener(v -> submitDonation());
        
        // Set up date picker
        expiryDateInput.setOnClickListener(v -> showDatePicker());
        expiryDateInput.setFocusable(false); // Prevent keyboard from showing up

        // Set up time picker
        pickupTimeInput.setOnClickListener(v -> showTimePicker());
        pickupTimeInput.setFocusable(false);
    }

    private void initializeViews(View view) {
        nameInput = view.findViewById(R.id.name_input);
        foodCategoryInput = view.findViewById(R.id.food_category_input);
        expiryDateInput = view.findViewById(R.id.expiry_date_input);
        quantityInput = view.findViewById(R.id.quantity_input);
        pickupTimeInput = view.findViewById(R.id.pickup_time_input);
        locationInput = view.findViewById(R.id.location_input);
        submitButton = view.findViewById(R.id.submit_button);
        backButton = view.findViewById(R.id.back_button);
        foodImageView = view.findViewById(R.id.food_image);
        Button uploadImageButton = view.findViewById(R.id.upload_image_button);

        uploadImageButton.setOnClickListener(v -> openImagePicker());
    }

    private void submitDonation() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        View view = getView();
        if (view == null) {
            Toast.makeText(getContext(), "Error: View not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading indicator
        ProgressBar progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        submitButton.setEnabled(false);

        if (selectedImageUri != null) {
            // Upload image first
            String imageFileName = "food_images/" + System.currentTimeMillis() + ".jpg";
            StorageReference imageRef = storageRef.child(imageFileName);

            imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get download URL
                    imageRef.getDownloadUrl()
                        .addOnSuccessListener(downloadUri -> {
                            // Create and save donation with image URL
                            saveDonationWithImage(downloadUri.toString());
                            progressBar.setVisibility(View.GONE);
                            submitButton.setEnabled(true);
                        })
                        .addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            submitButton.setEnabled(true);
                            Toast.makeText(getContext(), 
                                "Failed to get image URL: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    submitButton.setEnabled(true);
                    Toast.makeText(getContext(), 
                        "Failed to upload image: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                });
        } else {
            // Save donation without image
            saveDonationWithImage(null);
            progressBar.setVisibility(View.GONE);
            submitButton.setEnabled(true);
        }
    }

    private void saveDonationWithImage(String imageUrl) {
        String name = nameInput.getText().toString();
        String foodCategory = foodCategoryInput.getText().toString();
        String expiredDate = expiryDateInput.getText().toString();
        String quantity = quantityInput.getText().toString();
        String pickupTime = pickupTimeInput.getText().toString();
        String location = locationInput.getText().toString();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "You must be logged in to donate", Toast.LENGTH_SHORT).show();
            return;
        }

        String ownerUsername = currentUser.getDisplayName();
        String ownerProfileImageUrl = currentUser.getPhotoUrl() != null ? 
            currentUser.getPhotoUrl().toString() : "";

        // Create new donation with all fields including profile image URL
        DonationItem newDonation = new DonationItem(
            name,
            foodCategory,
            expiredDate,
            quantity,
            pickupTime,
            location,
            R.drawable.placeholder_image, // Default placeholder
            imageUrl,
            ownerUsername,
            ownerProfileImageUrl  // Add the profile image URL
        );

        // Save to repository
        DonationItemRepository repository = new DonationItemRepository();
        repository.addDonationItem(newDonation, new DonationItemRepository.OnDonationCompleteListener() {
            @Override
            public void onDonationSuccess() {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Donation added successfully", Toast.LENGTH_SHORT).show();
                    // Clear form or navigate back
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            }

            @Override
            public void onDonationFailure(Exception e) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), 
                        "Failed to add donation: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateInputs() {
        if (nameInput.getText().toString().trim().isEmpty()) {
            nameInput.setError("Name is required");
            return false;
        }
        if (foodCategoryInput.getText().toString().trim().isEmpty()) {
            foodCategoryInput.setError("Food category is required");
            return false;
        }
        if (expiryDateInput.getText().toString().trim().isEmpty()) {
            expiryDateInput.setError("Expiry date is required");
            return false;
        }
        if (quantityInput.getText().toString().trim().isEmpty()) {
            quantityInput.setError("Quantity is required");
            return false;
        }
        if (pickupTimeInput.getText().toString().trim().isEmpty()) {
            pickupTimeInput.setError("Pickup time is required");
            return false;
        }
        if (locationInput.getText().toString().trim().isEmpty()) {
            locationInput.setError("Location is required");
            return false;
        }
        return true;
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            requireContext(),
            R.style.CustomPickerTheme,
            (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateExpiryDateLabel();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date as today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        
        datePickerDialog.show();
    }

    private void updateExpiryDateLabel() {
        expiryDateInput.setText(dateFormatter.format(calendar.getTime()));
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            requireContext(),
            R.style.CustomPickerTheme,
            (view, hourOfDay, minute) -> {
                timeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                timeCalendar.set(Calendar.MINUTE, minute);
                updateTimeLabel();
            },
            timeCalendar.get(Calendar.HOUR_OF_DAY),
            timeCalendar.get(Calendar.MINUTE),
            false
        );
        
        timePickerDialog.show();
    }

    private void updateTimeLabel() {
        pickupTimeInput.setText(timeFormatter.format(timeCalendar.getTime()));
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
} 