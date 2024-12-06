package com.example.aplikacionandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;

/**
 * LoginActivity.java
 * <p>
 * This class provides the login functionality for the Android application, enabling users to:
 * - Enter their email and password for authentication.
 * - Reset their password if forgotten.
 * - Display or hide the password in the input field.
 * - Authenticate credentials via Firebase.
 * - Validate email verification before granting access.
 * - Send OTP for additional security during login.
 * - Provide user feedback through UI elements and Toast messages.
 */

public class LoginActivity extends AppCompatActivity {
    private EditText editTextLoginEmail, editTextLoginPwd;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "LoginActivity";

    /**
     * Initializes the activity, sets up UI components, and defines button actions.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Login");
        }

        editTextLoginEmail = findViewById(R.id.editText_login_email);
        editTextLoginPwd = findViewById(R.id.editText_login_pwd);
        progressBar = findViewById(R.id.progressBar);
        authProfile = FirebaseAuth.getInstance();

        Button buttonForgotPassword = findViewById(R.id.button_forgot_password);
        buttonForgotPassword.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "You can reset your password now!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

        ImageView imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowHidePwd.setOnClickListener(v -> {
            if (editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
            } else {
                editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
            }
        });

        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(v -> {
            String textEmail = editTextLoginEmail.getText().toString();
            String textPwd = editTextLoginPwd.getText().toString();

            if (TextUtils.isEmpty(textEmail)) {
                Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                editTextLoginEmail.setError("Email is required");
                editTextLoginEmail.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                Toast.makeText(LoginActivity.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                editTextLoginEmail.setError("Valid email is required");
                editTextLoginEmail.requestFocus();
            } else if (TextUtils.isEmpty(textPwd)) {
                Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                editTextLoginPwd.setError("Password is required");
                editTextLoginPwd.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                loginUser(textEmail, textPwd);
            }
        });
    }

    /**
     * Authenticates the user using Firebase and manages post-login actions.
     *
     * @param email User's email address.
     * @param pwd   User's password.
     */
    private void loginUser(String email, String pwd) {
        authProfile.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = authProfile.getCurrentUser();
                if (firebaseUser.isEmailVerified()) {
                    String otp = String.format("%06d", new Random().nextInt(999999));
                    SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    sharedPreferences.edit().putString("otp", otp).apply();
                    sendOtpToEmail(firebaseUser.getEmail(), otp);

                    Intent intent = new Intent(LoginActivity.this, OtpVerificationActivity.class);
                    intent.putExtra("email", firebaseUser.getEmail());
                    startActivity(intent);
                    finish();
                } else {
                    firebaseUser.sendEmailVerification();
                    authProfile.signOut();
                    showAlertDialog();
                }
            } else {
                progressBar.setVisibility(View.GONE);
                String errorMessage = "Login failed. Please try again.";
                if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                    errorMessage = "No account found with this email.";
                } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    errorMessage = "Invalid password.";
                }
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now. You can't login without email verification.");
        builder.setPositiveButton("Continue", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        builder.create().show();
    }

    private void sendOtpToEmail(String email, String otp) {
        String subject = "Your OTP for Login";
        String message = "Hello,\n\n" +
                "We received a request to log in to your account. To complete the process, please use the following OTP (One-Time Password):\n\n" +
                otp + "\n\n" +
                "This OTP is valid for the next 10 minutes. Please do not share this code with anyone for security reasons.\n\n" +
                "If you did not request this OTP, please ignore this email. No action is required on your part.\n\n" +
                "This is an automated message, so please do not reply to this email. If you have any questions or concerns, feel free to contact our support team.\n\n" +
                "Thank you for using our application.\n\n" +
                "Best regards,\n" +
                "The OneNote App Team";

        new JavaMailAPI(LoginActivity.this, email, subject, message).execute();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (authProfile.getCurrentUser() != null) {
            Toast.makeText(this, "Already logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, NotesActivity.class));
            finish();
        } else {
            Toast.makeText(this, "You can login now!", Toast.LENGTH_SHORT).show();
        }
    }
}
