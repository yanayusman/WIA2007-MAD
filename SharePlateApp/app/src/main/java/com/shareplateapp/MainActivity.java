package com.shareplateapp;

import android.content.Intent;
import android.os.Bundle;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.Button;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textview.MaterialTextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ConstraintLayout constraintLayout = findViewById(R.id.main);
        
        // Check if the background is actually an AnimationDrawable
        if (constraintLayout.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
            animationDrawable.setEnterFadeDuration(2500);
            animationDrawable.setExitFadeDuration(5000);
            animationDrawable.start();
        }

        MaterialTextView logInLink = findViewById(R.id.logInLink);
        logInLink.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}