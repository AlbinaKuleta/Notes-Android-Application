package com.example.aplikacionandroid;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Handler;

/**
 * AnimationActivity serves as a splash screen, displaying a brief animation
 * before navigating the user to the MainActivity.
 * This activity enhances the user experience by providing a transitional
 * screen during the app's startup process.
 */

public class AnimationActivity extends AppCompatActivity {
    /**
     * Initializes the splash screen, sets the layout, and schedules a transition
     * to the MainActivity after a predefined delay.
     *
     * @param savedInstanceState Data about the activity's previously saved state, if available.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        // Set the title of the activity in the action bar
        getSupportActionBar().setTitle("One Note Application");

        // Schedule a transition to MainActivity after a 3-second delay

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(AnimationActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}