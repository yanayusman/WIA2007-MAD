package com.shareplateapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Log;
import android.net.Uri;
import android.provider.MediaStore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.bumptech.glide.Glide;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import android.graphics.drawable.Drawable;
import com.bumptech.glide.RequestBuilder;

public class ProfilePage extends Fragment {
    private static final String TAG = "ProfilePage";
    private ImageView profileImage;
    private TextView username;
    private TextView email;
    private MaterialButton signOutButton;
    private FirebaseAuth mAuth;
    private ImageView editProfileImage;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    uploadProfileImage(selectedImageUri);
                }
            }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_page, container, false);

        Log.d(TAG, "onCreateView: Initializing views");
        
        // Initialize views
        profileImage = view.findViewById(R.id.profile_image);
        setupProfileImageClick();
        username = view.findViewById(R.id.username);
        email = view.findViewById(R.id.email);
        signOutButton = view.findViewById(R.id.signOutButton);
        if (signOutButton == null) {
            Log.e(TAG, "onCreateView: signOutButton not found in layout");
        } else {
            Log.d(TAG, "onCreateView: signOutButton found and initialized");
            signOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: Sign out button clicked");
                    signOut();
                }
            });
        }

        editProfileImage = view.findViewById(R.id.edit_profile_image);
        editProfileImage.setOnClickListener(v -> openImagePicker());

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::refreshProfile);
        
        // Set the colors for the refresh animation
        swipeRefreshLayout.setColorSchemeResources(
            R.color.button_green,  // Use your app's primary color
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        );

        // Set up user profile
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Set email
            email.setText("Email: " + currentUser.getEmail());
            
            // Set display name if available, otherwise use email
            String displayName = currentUser.getDisplayName();
            username.setText(displayName != null && !displayName.isEmpty() ? 
                displayName : currentUser.getEmail());

            // Load existing profile image if available
            loadProfileImage(currentUser.getUid());
        }

        return view;
    }

    private void signOut() {
        Log.d(TAG, "signOut: Showing confirmation dialog");
        
        // Create and show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Sign Out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Yes", (dialog, which) -> {
                Log.d(TAG, "signOut: User confirmed sign out");
                performSignOut();
            })
            .setNegativeButton("No", (dialog, which) -> {
                Log.d(TAG, "signOut: User cancelled sign out");
                dialog.dismiss();
            })
            .show();
    }

    private void performSignOut() {
        Log.d(TAG, "performSignOut: Attempting to sign out user");
        try {
            mAuth.signOut();
            Log.d(TAG, "performSignOut: User signed out successfully");
            Toast.makeText(getContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();
            
            // Navigate to MainActivity
            Intent intent = new Intent(getActivity(), MainActivity.class);
            // Clear the back stack
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            
            // Finish the current activity
            if (getActivity() != null) {
                getActivity().finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "performSignOut: Failed to sign out", e);
            Toast.makeText(getContext(), "Sign out failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void uploadProfileImage(Uri imageUri) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        StorageReference imageRef = storageRef.child("profile_images/" + userId + ".jpg");

        // Show loading state
        if (getView() != null) {
            getView().findViewById(R.id.edit_profile_image).setEnabled(false);
            // Show the refresh animation
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(true);
            }
        }

        // Upload the new image
        imageRef.putFile(imageUri)
            .addOnSuccessListener(taskSnapshot -> {
                // Get download URL and update profile
                imageRef.getDownloadUrl()
                    .addOnSuccessListener(downloadUri -> {
                        // Update Firebase Auth user profile
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUri)
                            .build();
                        
                        currentUser.updateProfile(profileUpdates)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "User profile photo updated successfully");
                                // Force refresh the user data
                                currentUser.reload().addOnCompleteListener(task -> {
                                    // Update UI after reload
                                    updateProfileImage(downloadUri.toString());
                                    // Refresh the entire profile
                                    refreshProfile();
                                });
                                // Update all donations with the new profile image URL
                                updateDonationsProfileImage(downloadUri.toString(), currentUser.getDisplayName());
                                
                                // Save the profile image URL to SharedPreferences
                                if (getContext() != null) {
                                    getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                        .edit()
                                        .putString("default_profile_image", downloadUri.toString())
                                        .apply();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to update user profile photo", e);
                                if (swipeRefreshLayout != null) {
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            });

                        // Show success message
                        Toast.makeText(getContext(), "Profile picture updated successfully", 
                            Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to get download URL", e);
                        Toast.makeText(getContext(), 
                            "Failed to update profile picture: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                        if (swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Failed to upload image", e);
                Toast.makeText(getContext(), 
                    "Failed to upload image: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            })
            .addOnCompleteListener(task -> {
                // Re-enable the edit button
                if (getView() != null) {
                    getView().findViewById(R.id.edit_profile_image).setEnabled(true);
                }
            });
    }

    private void updateProfileImage(String imageUrl) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        // Update the ImageView
        if (profileImage != null && getContext() != null) {
            Glide.with(getContext())
                .load(imageUrl)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Enable disk caching
                .skipMemoryCache(false) // Enable memory caching
                .into(profileImage);
            
            // Stop the refresh animation if it's running
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void loadProfileImage(String userId) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        
        // Use a single Glide request builder for all cases
        RequestBuilder<Drawable> glideRequest = Glide.with(this)
            .load(R.drawable.profile) // Default placeholder
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL) // Enable disk caching
            .skipMemoryCache(false); // Enable memory caching

        // Try loading from SharedPreferences first
        String savedProfileImageUrl = getContext() != null ? 
            getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                .getString("default_profile_image", null) : null;
            
        if (savedProfileImageUrl != null) {
            glideRequest.load(savedProfileImageUrl)
                .into(profileImage);
            return;
        }
        
        // Then try Firebase Auth
        if (currentUser != null && currentUser.getPhotoUrl() != null) {
            glideRequest.load(currentUser.getPhotoUrl())
                .into(profileImage);
            return;
        }

        // Fallback to storage if no other source is available
        StorageReference imageRef = storageRef.child("profile_images/" + userId + ".jpg");
        
        imageRef.getDownloadUrl()
            .addOnSuccessListener(uri -> {
                if (getContext() != null && profileImage != null) {
                    // Save the URL to SharedPreferences for faster future loads
                    getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .putString("default_profile_image", uri.toString())
                        .apply();
                        
                    glideRequest.load(uri)
                        .into(profileImage);
                }
            })
            .addOnFailureListener(e -> {
                Log.d(TAG, "No profile image found for user: " + userId);
                if (getContext() != null && profileImage != null) {
                    glideRequest.into(profileImage);
                }
            });
    }

    private void refreshProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Refresh user data
            email.setText("Email: " + currentUser.getEmail());
            
            String displayName = currentUser.getDisplayName();
            username.setText(displayName != null && !displayName.isEmpty() ? 
                displayName : currentUser.getEmail());

            // Reload profile image
            loadProfileImage(currentUser.getUid());

            // Optional: Refresh any other user data you want to update
            
            // End the refreshing animation
            swipeRefreshLayout.setRefreshing(false);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            // Handle the case where user is not logged in
            Toast.makeText(getContext(), "Please log in to refresh profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDonationsProfileImage(String newProfileImageUrl, String ownerUsername) {
        // Get reference to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        // Query all donations by this user
        db.collection("allDonationItems")
            .whereEqualTo("ownerUsername", ownerUsername)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                // Batch write to update all documents
                WriteBatch batch = db.batch();
                
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    batch.update(document.getReference(), 
                        "ownerProfileImageUrl", newProfileImageUrl);
                }
                
                // Commit the batch
                batch.commit()
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Successfully updated all donations with new profile image");
                        
                        // Notify any active FoodItemDetailFragment instances to refresh
                        if (getActivity() != null) {
                            // Create an intent to broadcast the profile image update
                            Intent intent = new Intent("profile.image.updated");
                            intent.putExtra("newProfileImageUrl", newProfileImageUrl);
                            intent.putExtra("ownerUsername", ownerUsername);
                            
                            // Send local broadcast
                            LocalBroadcastManager.getInstance(getActivity())
                                .sendBroadcast(intent);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error updating donations with new profile image", e);
                    });
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error querying donations", e);
            });
    }

    private void setupProfileImageClick() {
        if (profileImage != null) {
            profileImage.setOnClickListener(v -> {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    // Get profile image URL from SharedPreferences or Firebase
                    String imageUrl = getContext() != null ? 
                        getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                            .getString("default_profile_image", null) : null;
                    
                    // If no URL in SharedPreferences, try Firebase
                    if (imageUrl == null && currentUser.getPhotoUrl() != null) {
                        imageUrl = currentUser.getPhotoUrl().toString();
                    }

                    // Create and show FullScreenImageFragment
                    FullScreenImageFragment fullScreenFragment = 
                        FullScreenImageFragment.newInstance(imageUrl, R.drawable.profile);
                    requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fullScreenFragment)
                        .addToBackStack(null)
                        .commit();
                }
            });
        }
    }
}
