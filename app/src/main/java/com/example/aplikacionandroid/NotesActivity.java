package com.example.aplikacionandroid;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplikacionandroid.utils.NotificationUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * NotesActivity
 * <p>
 * This class represents the main activity for managing notes in the Android application.
 * It handles the display, creation, updating, and deletion of notes, as well as user authentication,
 * notification management, and interaction with Firebase Realtime Database.
 */

public class NotesActivity extends AppCompatActivity {
    private static final int POST_NOTIFICATION_PERMISSION_REQUEST_CODE = 1;
    private FirebaseAuth authProfile;
    private FirebaseDatabase database;
    private String userId;

    /**
     * onCreate
     * <p>
     * Initializes the activity, checks user authentication, sets up the RecyclerView for notes,
     * and creates a notification channel. Handles the addition and display of notes.
     *
     * @param savedInstanceState Contains the data most recently supplied in `onSaveInstanceState()`.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        getSupportActionBar().setTitle("One Note Application");

        FirebaseApp.initializeApp(this);
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser currentUser = authProfile.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Please log in first!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }


        userId = currentUser.getUid();
        database = FirebaseDatabase.getInstance();

        createNotificationChannel();

        FloatingActionButton add = findViewById(R.id.addNote);
        TextView empty = findViewById(R.id.empty);
        RecyclerView recyclerView = findViewById(R.id.recycler);

        add.setOnClickListener(view -> openAddNoteDialog());

        database.getReference("Registered User").child(userId).child("notes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Note> noteList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Note note = dataSnapshot.getValue(Note.class);
                            Objects.requireNonNull(note).setKey(dataSnapshot.getKey());
                            noteList.add(note);
                        }

                        if (noteList.isEmpty()) {
                            empty.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            empty.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }

                        NoteAdapter adapter = new NoteAdapter(NotesActivity.this, noteList);
                        recyclerView.setAdapter(adapter);

                        adapter.setOnItemClickListener(note -> openEditNoteDialog(note));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(NotesActivity.this, "Error loading notes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * openAddNoteDialog
     * <p>
     * Opens a dialog for the user to input a new note's title and content.
     */
    private void openAddNoteDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.add_note_dialog, null);
        TextInputLayout titleLayout = view.findViewById(R.id.titleLayout);
        TextInputLayout contentLayout = view.findViewById(R.id.contentLayout);
        TextInputEditText titleET = view.findViewById(R.id.titleET);
        TextInputEditText contentET = view.findViewById(R.id.contentET);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Add Note")
                .setView(view)
                .setPositiveButton("Add", (dialogInterface, i) -> {
                    String title = Objects.requireNonNull(titleET.getText()).toString();
                    String content = Objects.requireNonNull(contentET.getText()).toString();

                    if (title.isEmpty()) {
                        titleLayout.setError("This field is required!");
                    } else if (content.isEmpty()) {
                        contentLayout.setError("This field is required!");
                    } else {
                        storeNoteInDatabase(new Note(title, content));
                        dialogInterface.dismiss();


                    }
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .create();
        alertDialog.show();
    }

    /**
     * requestNotificationPermissionAndShow
     * <p>
     * Requests POST_NOTIFICATIONS permission and displays a notification for the created note.
     *
     * @param noteTitle The title of the note for which the notification is displayed.
     */
    private void requestNotificationPermissionAndShow(String noteTitle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Request the permission if not already granted
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, POST_NOTIFICATION_PERMISSION_REQUEST_CODE);
            } else {
                showNotification(noteTitle);
            }
        } else {
            showNotification(noteTitle);
        }
    }

    /**
     * showNotification
     * <p>
     * Displays a notification with details of the created note.
     *
     * @param noteTitle The title of the note to include in the notification.
     */
    private void showNotification(String noteTitle) {
        String channelId = "note_creation_channel";

        String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        // Check if the POST_NOTIFICATIONS permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted; do nothing
            Toast.makeText(this, "Notification permission not granted!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build and display the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Note Created")
                .setContentText("Title: " + noteTitle + "\nCreated at: " + timestamp)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    /**
     * createNotificationChannel
     * <p>
     * Creates a notification channel for displaying notifications about note creation events.
     * Required for Android 8.0 (API level 26) and higher.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "note_creation_channel";
            String channelName = "Note Creation Notifications";
            String channelDescription = "Notifications for note creation events";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * openEditNoteDialog
     * <p>
     * Opens a dialog for editing an existing note or deleting it.
     *
     * @param note The `Note` object to edit or delete.
     */
    private void openEditNoteDialog(Note note) {
        View view = LayoutInflater.from(this).inflate(R.layout.add_note_dialog, null);
        TextInputLayout titleLayout = view.findViewById(R.id.titleLayout);
        TextInputLayout contentLayout = view.findViewById(R.id.contentLayout);
        TextInputEditText titleET = view.findViewById(R.id.titleET);
        TextInputEditText contentET = view.findViewById(R.id.contentET);

        titleET.setText(note.getTitle());
        contentET.setText(note.getContent());

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Edit Note")
                .setView(view)
                .setPositiveButton("Save", (dialogInterface, i) -> {
                    String title = Objects.requireNonNull(titleET.getText()).toString();
                    String content = Objects.requireNonNull(contentET.getText()).toString();

                    if (title.isEmpty()) {
                        titleLayout.setError("This field is required!");
                    } else if (content.isEmpty()) {
                        contentLayout.setError("This field is required!");
                    } else {
                        updateNoteInDatabase(note.getKey(), new Note(title, content));
                        dialogInterface.dismiss();
                    }
                })
                .setNeutralButton("Close", (dialogInterface, i) -> dialogInterface.dismiss())
                .setNegativeButton("Delete", (dialogInterface, i) -> deleteNoteFromDatabase(note.getKey()))
                .create();
        alertDialog.show();
    }

    /**
     * storeNoteInDatabase
     * <p>
     * Stores a new note in the Firebase Realtime Database under the current user's notes.
     *
     * @param note The `Note` object to store.
     */
    private void storeNoteInDatabase(Note note) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Storing note...");
        dialog.show();

        database.getReference("Registered User").child(userId).child("notes")
                .push()
                .setValue(note)
                .addOnSuccessListener(unused -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Note added successfully!", Toast.LENGTH_SHORT).show();

                    // Save the notification
                    NotificationUtils.saveNotification(this, "Note created", note.getTitle());
                    incrementBadgeCount();

                    // Refresh badge
                    invalidateOptionsMenu();
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Failed to add note!", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * incrementBadgeCount
     * <p>
     * Increments the unread notification badge count stored in SharedPreferences.
     */
    private void incrementBadgeCount() {
        SharedPreferences prefs = getSharedPreferences("notifications_pref", Context.MODE_PRIVATE);
        int unreadCount = prefs.getInt("unread_count", 0);
        prefs.edit().putInt("unread_count", unreadCount + 1).apply(); // Increment unread count
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    /**
     * updateBadge
     * <p>
     * Updates the badge UI element with the current unread notification count.
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
     * updateNoteInDatabase
     * <p>
     * Updates an existing note in the Firebase Realtime Database.
     *
     * @param key  The unique key of the note to update.
     * @param note The updated `Note` object.
     */
    private void updateNoteInDatabase(String key, Note note) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Updating note...");
        dialog.show();

        database.getReference("Registered User").child(userId).child("notes")
                .child(key)
                .setValue(note)
                .addOnSuccessListener(unused -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Note updated successfully!", Toast.LENGTH_SHORT).show();

                    // Save notification for note update
                    NotificationUtils.saveNotification(this, "Note updated", note.getTitle());
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Failed to update note!", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * deleteNoteFromDatabase
     * <p>
     * Deletes an existing note from the Firebase Realtime Database.
     *
     * @param key The unique key of the note to delete.
     */
    private void deleteNoteFromDatabase(String key) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Deleting note...");
        dialog.show();

        database.getReference("Registered User").child(userId).child("notes").child(key)
                .removeValue()
                .addOnSuccessListener(unused -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Note deleted successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Failed to delete note!", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * onCreateOptionsMenu
     * <p>
     * Inflates the options menu and sets up the notification badge.
     *
     * @param menu The options menu in which items are placed.
     * @return true to display the menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);

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
            startActivity(new Intent(NotesActivity.this, NotificationsActivity.class));

            // Clear the badge count after clicking
            clearBadgeCount();
            badge.setVisibility(View.GONE); // Hide the badge from the UI
        });

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * clearBadgeCount
     * <p>
     * Clears the unread notification badge count stored in SharedPreferences.
     */
    private void clearBadgeCount() {
        SharedPreferences prefs = getSharedPreferences("notifications_pref", Context.MODE_PRIVATE);
        prefs.edit().putInt("unread_count", 0).apply();
    }

    /**
     * onOptionsItemSelected
     * <p>
     * Handles menu item selection, including navigation to various activities.
     *
     * @param item The selected menu item.
     * @return true if the item is handled successfully.
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
            Intent intent = new Intent(NotesActivity.this, NotificationsActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_profile) {
            Intent intent = new Intent(NotesActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(NotesActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(NotesActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(NotesActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(NotesActivity.this, "Logged Out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(NotesActivity.this, MainActivity.class);

            //clear stack to prevent user coming back to UserProfileActivity on pressing back button after logging out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); //close user profile activity
        } else {
            Toast.makeText(NotesActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();

        }
        return super.onOptionsItemSelected(item);
    }
}

