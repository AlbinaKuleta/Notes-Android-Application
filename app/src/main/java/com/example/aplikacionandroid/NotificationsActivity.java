/**
 * This class represents the NotesActivity in the application, which manages the display,
 * addition, editing, and deletion of notes. It integrates Firebase for authentication
 * and database management and handles notifications for user actions.
 */
package com.example.aplikacionandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplikacionandroid.utils.NotificationUtils;

public class NotificationsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView noNotifications;

    /**
     * Initializes the activity, sets up Firebase authentication and database,
     * and manages UI elements like adding, editing, and displaying notes.
     *
     * @param savedInstanceState Saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        getSupportActionBar().setTitle("One Note Application");

        recyclerView = findViewById(R.id.recycler_notifications);
        noNotifications = findViewById(R.id.no_notifications);

        fetchNotifications();

        Button clearNotifications = findViewById(R.id.clear_notifications);
        clearNotifications.setOnClickListener(v -> {
            NotificationUtils.clearNotifications(this);
            noNotifications.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        });

        NotificationUtils.clearBadge(this);
    }

    /**
     * Fetches the list of notifications and updates the UI with the retrieved data.
     */
    private void fetchNotifications() {
        NotificationUtils.getNotifications(this, notifications -> {
            if (notifications.isEmpty()) {
                noNotifications.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                noNotifications.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                NotificationsAdapter adapter = new NotificationsAdapter(notifications);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(adapter);
            }
        });
    }

}
