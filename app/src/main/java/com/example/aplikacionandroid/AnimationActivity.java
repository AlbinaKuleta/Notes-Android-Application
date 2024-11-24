package com.example.aplikacionandroid;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Handler;


public class AnimationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(AnimationActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}