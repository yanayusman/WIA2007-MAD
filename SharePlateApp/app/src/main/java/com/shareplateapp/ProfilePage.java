package com.shareplateapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfilePage extends Fragment {
    private static final String TAG = "ProfilePage";
    private ImageView profileImage;
    private TextView username;
    private TextView email;
    private MaterialButton signOutButton;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_page, container, false);

        Log.d(TAG, "onCreateView: Initializing views");
        
        // Initialize views
        profileImage = view.findViewById(R.id.profile_image);
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

        // Set up user profile
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Set email
            email.setText("Email: " + currentUser.getEmail());
            
            // Set display name if available, otherwise use email
            String displayName = currentUser.getDisplayName();
            username.setText(displayName != null && !displayName.isEmpty() ? 
                displayName : currentUser.getEmail());
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
}
