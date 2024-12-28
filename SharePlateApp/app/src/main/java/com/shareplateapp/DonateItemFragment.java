package com.shareplateapp;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        donationItemRepository = new DonationItemRepository();
        calendar = Calendar.getInstance();
        timeCalendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        timeFormatter = new SimpleDateFormat("hh:mm a", Locale.US); // 12-hour format with AM/PM
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_donate_item, container, false);
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
    }

    private void submitDonation() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        // Format the data - without prefixes
        String name = nameInput.getText().toString().trim();
        String foodCategory = foodCategoryInput.getText().toString().trim();
        String expiryDate = expiryDateInput.getText().toString().trim();
        String quantity = quantityInput.getText().toString().trim();
        String pickupTime = pickupTimeInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();

        // Create new DonationItem
        DonationItem newDonation = new DonationItem(
            name,
            foodCategory,
            expiryDate,
            quantity,
            pickupTime,
            location,
            R.drawable.placeholder_image  // Default placeholder image
        );

        // Add to Firebase
        donationItemRepository.addDonationItem(newDonation);

        // Show success message and navigate back
        Toast.makeText(getContext(), "Donation submitted successfully!", Toast.LENGTH_SHORT).show();
        requireActivity().getSupportFragmentManager().popBackStack();
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
} 