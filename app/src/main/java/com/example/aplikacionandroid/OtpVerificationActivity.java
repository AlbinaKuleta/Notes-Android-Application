package com.example.aplikacionandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This activity handles the OTP verification process for user authentication.
 */
public class OtpVerificationActivity extends AppCompatActivity {
    private EditText editTextOtp;
    private Button buttonVerifyOtp;
    private ProgressBar progressBar;

    /**
     * Called when the activity is first created.
     * Initializes UI elements and sets up the OTP verification logic.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied. Otherwise, null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Verify OTP");
        setContentView(R.layout.activity_otp_verification);
        editTextOtp = findViewById(R.id.editText_otp);
        buttonVerifyOtp = findViewById(R.id.button_verify_otp);
        progressBar = findViewById(R.id.progressBar);
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String savedOtp = sharedPreferences.getString("otp", "");
        buttonVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                buttonVerifyOtp.setEnabled(false);
                String enteredOtp = editTextOtp.getText().toString().trim();
                new android.os.Handler().postDelayed(() -> {
                    if (enteredOtp.equals(savedOtp)) {
                        progressBar.setVisibility(View.GONE);
                        buttonVerifyOtp.setEnabled(true);
                        Toast.makeText(OtpVerificationActivity.this, "OTP Verified Successfully!", Toast.LENGTH_SHORT).show();
                        Toast.makeText(OtpVerificationActivity.this, "You are logged in now", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(OtpVerificationActivity.this, NotesActivity.class));
                        finish();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        buttonVerifyOtp.setEnabled(true);
                        Toast.makeText(OtpVerificationActivity.this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }, 2000);
            }
        });
    }
}