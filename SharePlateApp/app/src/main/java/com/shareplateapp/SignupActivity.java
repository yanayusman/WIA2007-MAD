package com.shareplateapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthException;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button signupButton;
    private TextView cancelTextView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signupButton = findViewById(R.id.loginButton);
        cancelTextView = findViewById(R.id.cancelTextView);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(SignupActivity.this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show loading state
                signupButton.setEnabled(false);

                // Create user with email and password
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    // Send verification email
                                    user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                signupButton.setEnabled(true);
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SignupActivity.this,
                                                            "Account created! Please check your email to verify your account.",
                                                            Toast.LENGTH_LONG).show();
                                                    // Sign out until email is verified
                                                    mAuth.signOut();
                                                    // Go to login screen
                                                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Log.e(TAG, "sendEmailVerification", task.getException());
                                                    Toast.makeText(SignupActivity.this,
                                                            "Failed to send verification email.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                }
                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                signupButton.setEnabled(true);
                                String errorMessage;
                                if (task.getException() instanceof FirebaseAuthException) {
                                    FirebaseAuthException e = (FirebaseAuthException) task.getException();
                                    errorMessage = getErrorMessage(e.getErrorCode());
                                } else {
                                    errorMessage = task.getException() != null ? 
                                        task.getException().getMessage() : 
                                        "Sign up failed";
                                }
                                Toast.makeText(SignupActivity.this, errorMessage,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            }
        });

        cancelTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private String getErrorMessage(String errorCode) {
        switch (errorCode) {
            case "ERROR_INVALID_EMAIL":
                return "The email address is badly formatted.";
            case "ERROR_EMAIL_ALREADY_IN_USE":
                return "The email address is already in use by another account.";
            case "ERROR_WEAK_PASSWORD":
                return "The password is too weak. Please use at least 6 characters.";
            default:
                return "Sign up failed: " + errorCode;
        }
    }
}