package com.example.aplikacionandroid;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aplikacionandroid.utils.NotificationUtils;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView noNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

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
