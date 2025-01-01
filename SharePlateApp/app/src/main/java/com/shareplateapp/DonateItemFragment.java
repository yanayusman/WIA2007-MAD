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
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.os.Environment;
import java.io.File;
import java.io.IOException;
import androidx.core.content.FileProvider;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.app.AlertDialog;

public class DonateItemFragment extends Fragment {
    protected EditText nameInput, foodCategoryInput, descriptionInput, expiryDateInput, 
                      quantityInput, pickupTimeInput, locationInput;
    protected Button submitButton;
    protected ImageView backButton;
    protected ImageView foodImageView;
    protected Uri selectedImageUri;
    protected ProgressBar progressBar;
    protected StorageReference storageRef;
    private DonationItemRepository repository;
    private Calendar timeCalendar;
    private SimpleDateFormat timeFormatter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repository = new DonationItemRepository();
        timeCalendar = Calendar.getInstance();
        timeFormatter = new SimpleDateFormat("hh:mm a", Locale.US);

        // Initialize Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        foodImageView.setImageURI(selectedImageUri);
                    }
                }
            }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donate_item, container, false);

        // Initialize views
        nameInput = view.findViewById(R.id.name_input);
        foodCategoryInput = view.findViewById(R.id.food_category_input);
        descriptionInput = view.findViewById(R.id.description_input); // Make sure this ID exists in your layout
        expiryDateInput = view.findViewById(R.id.expiry_date_input);
        quantityInput = view.findViewById(R.id.quantity_input);
        pickupTimeInput = view.findViewById(R.id.pickup_time_input);
        locationInput = view.findViewById(R.id.location_input);
        submitButton = view.findViewById(R.id.submit_button);
        backButton = view.findViewById(R.id.back_button);
        foodImageView = view.findViewById(R.id.food_image);
        progressBar = view.findViewById(R.id.progress_bar);

        // Setup click listeners and other initialization
        setupClickListeners();

        return view;
    }

    private void setupClickListeners() {
        // Setup back button click listener
        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Setup food image click listener for image selection
        foodImageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Setup submit button click listener
        submitButton.setOnClickListener(v -> submitDonation());

        // Setup pickup time input click listener
        pickupTimeInput.setOnClickListener(v -> showTimePickerDialog());

        // Setup expiry date input click listener
        expiryDateInput.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            getContext(),
            (view, hourOfDay, minute) -> {
                timeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                timeCalendar.set(Calendar.MINUTE, minute);
                pickupTimeInput.setText(timeFormatter.format(timeCalendar.getTime()));
            },
            timeCalendar.get(Calendar.HOUR_OF_DAY),
            timeCalendar.get(Calendar.MINUTE),
            false
        );
        timePickerDialog.show();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            requireContext(),
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                expiryDateInput.setText(dateFormat.format(calendar.getTime()));
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    protected void submitDonation() {
        if (!validateInputs()) return;

        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);
        submitButton.setEnabled(false);

        // Get current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Please login to donate", Toast.LENGTH_SHORT).show();
            return;
        }

        // If image was selected, upload it first
        if (selectedImageUri != null) {
            String imageFileName = "food_images/" + System.currentTimeMillis() + ".jpg";
            StorageReference imageRef = storageRef.child(imageFileName);

            imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> 
                    imageRef.getDownloadUrl()
                        .addOnSuccessListener(downloadUri -> {
                            createDonationWithImage(downloadUri.toString(), currentUser.getDisplayName());
                        })
                        .addOnFailureListener(e -> handleError("Failed to get download URL: " + e.getMessage())))
                .addOnFailureListener(e -> handleError("Failed to upload image: " + e.getMessage()));
        } else {
            createDonationWithImage(null, currentUser.getDisplayName());
        }
    }

    private void createDonationWithImage(String imageUrl, String ownerUsername) {
        DonationItem newDonation = new DonationItem(
            nameInput.getText().toString(),
            foodCategoryInput.getText().toString(),
            descriptionInput.getText().toString(),
            "", // category
            expiryDateInput.getText().toString(),
            quantityInput.getText().toString(),
            pickupTimeInput.getText().toString(),
            locationInput.getText().toString(),
            R.drawable.placeholder_image,
            imageUrl,
            ownerUsername,
            "food", // donateType
            "" // ownerProfileImageUrl - can be updated later if needed
        );

        repository.addDonationItem(newDonation, new DonationItemRepository.OnDonationCompleteListener() {
            @Override
            public void onDonationSuccess() {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Donation added successfully", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
                progressBar.setVisibility(View.GONE);
                submitButton.setEnabled(true);
            }

            @Override
            public void onDonationFailure(Exception e) {
                handleError("Failed to add donation: " + e.getMessage());
            }
        });
    }

    protected boolean validateInputs() {
        if (nameInput.getText().toString().trim().isEmpty()) {
            nameInput.setError("Name is required");
            return false;
        }
        if (foodCategoryInput.getText().toString().trim().isEmpty()) {
            foodCategoryInput.setError("Category is required");
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

    private void handleError(String errorMessage) {
        if (getContext() != null) {
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }
        progressBar.setVisibility(View.GONE);
        submitButton.setEnabled(true);
    }
} 
