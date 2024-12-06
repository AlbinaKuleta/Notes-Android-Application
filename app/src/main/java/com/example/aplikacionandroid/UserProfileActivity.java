package com.example.aplikacionandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * This activity displays the user's profile information and provides navigation options
 * to update the profile, email, or logout. It fetches user details from Firebase Authentication
 * and Firebase Realtime Database and provides an interactive menu for additional actions.
 */

public class UserProfileActivity extends AppCompatActivity {
    private TextView textViewWelcome, textViewFullName, textViewEmail, textViewDoB, textViewGender, textViewMobile;
    private ProgressBar progressBar;
    private String fullName, email, doB, gender, mobile;
    private FirebaseAuth authProfile;

    /**
     * Called when the activity is first created.
     * Initializes UI components and checks user authentication.
     *
     * @param savedInstanceState The saved instance state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().setTitle("My profile");

        textViewWelcome = findViewById(R.id.textView_show_welcome);
        textViewFullName = findViewById(R.id.textView_show_full_name);
        textViewEmail = findViewById(R.id.textView_show_email);
        textViewDoB = findViewById(R.id.textView_show_dob);
        textViewGender = findViewById(R.id.textView_show_gender);
        textViewMobile = findViewById(R.id.textView_show_mobile);
        progressBar = findViewById(R.id.progressBar);
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(UserProfileActivity.this, "Something went wrong! User's details are not available.",
                    Toast.LENGTH_LONG).show();
        } else {
            checkIfEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }

    /**
     * Checks if the user's email is verified. If not, prompts the user to verify their email.
     *
     * @param firebaseUser The current authenticated Firebase user.
     */
    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        if (!firebaseUser.isEmailVerified()) {
            showAlertDialog();
        }
    }

    /**
     * Displays an alert dialog prompting the user to verify their email.
     */
    private void showAlertDialog() {
        //setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now. You can't login without email verification next time.");

        //open email apps if user clicks/taps continue
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //To email app in new window and not within app
                startActivity(intent);
            }
        });

        //Alert dialog
        AlertDialog alertDialog = builder.create();
        //show the alert dialog
        alertDialog.show();
    }

    /**
     * Fetches and displays the user's profile information from Firebase Realtime Database.
     *
     * @param firebaseUser The current authenticated Firebase user.
     */
    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        //extracting user reference from database for "Registered Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered User");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    fullName = firebaseUser.getDisplayName();
                    email = firebaseUser.getEmail();
                    doB = readUserDetails.doB;
                    gender = readUserDetails.gender;
                    mobile = readUserDetails.mobile;

                    textViewWelcome.setText("Welcome, " + fullName + "!");
                    textViewFullName.setText(fullName);
                    textViewEmail.setText(email);
                    textViewDoB.setText(doB);
                    textViewGender.setText(gender);
                    textViewMobile.setText(mobile);

                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Updates the notification badge with the unread count.
     *
     * @param badge The TextView displaying the badge count.
     */
    private void updateBadge(TextView badge) {
        SharedPreferences prefs = getSharedPreferences("notifications_pref", Context.MODE_PRIVATE);
        int unreadCount = prefs.getInt("unread_count", 0); // Retrieve unread count

        if (unreadCount > 0) {
            badge.setText(String.valueOf(unreadCount));
            badge.setVisibility(View.VISIBLE);
        } else {
            badge.setVisibility(View.GONE);
        }
    }

    /**
     * Inflates the menu in the ActionBar and sets up notification badge functionality.
     *
     * @param menu The menu to be inflated.
     * @return True if the menu is created successfully.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.second_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_notifications);
        View actionView = menuItem.getActionView();

        if (actionView == null) {
            actionView = getLayoutInflater().inflate(R.layout.badge_icon, null);
            menuItem.setActionView(actionView);
        }

        TextView badge = actionView.findViewById(R.id.notification_badge);
        updateBadge(badge); // Update the badge with the current unread count

        actionView.setOnClickListener(v -> {
            // Open the NotificationsActivity
            startActivity(new Intent(UserProfileActivity.this, NotificationsActivity.class));

            // Clear the badge count after clicking
            clearBadgeCount();
            badge.setVisibility(View.GONE); // Hide the badge from the UI
        });

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Clears the notification badge count by resetting it to zero.
     */
    private void clearBadgeCount() {
        SharedPreferences prefs = getSharedPreferences("notifications_pref", Context.MODE_PRIVATE);
        prefs.edit().putInt("unread_count", 0).apply();
    }

    /**
     * Handles the selection of menu items in the ActionBar.
     *
     * @param item The selected menu item.
     * @return True if the item is handled successfully.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_refresh) {
            //refresh activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
        } else if (id == R.id.menu_notifications) {
            Intent intent = new Intent(UserProfileActivity.this, NotificationsActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_notes) {
            Intent intent = new Intent(UserProfileActivity.this, NotesActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(UserProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(UserProfileActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(UserProfileActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(UserProfileActivity.this, "Logged Out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);

            //clear stack to prevent user coming back to UserProfileActivity on pressing back button after logging out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); //close user profile activity
        } else {
            Toast.makeText(UserProfileActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();

        }
        return super.onOptionsItemSelected(item);
    }
}