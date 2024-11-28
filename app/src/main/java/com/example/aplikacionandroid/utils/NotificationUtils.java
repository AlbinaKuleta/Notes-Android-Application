package com.example.aplikacionandroid.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class NotificationUtils {
    private static final String NOTIFICATIONS_PATH = "Registered User";

    public static void saveNotification(Context context, String title, String content) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("NotificationUtils", "User is not logged in!");
            Toast.makeText(context, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        String notificationId = UUID.randomUUID().toString();

        HashMap<String, String> notificationData = new HashMap<>();
        notificationData.put("title", title);
        notificationData.put("content", content);
        notificationData.put("timestamp", timestamp);

        FirebaseDatabase.getInstance().getReference("Registered User")
                .child(userId)
                .child("notifications") // Use "notifications" instead of "notes"
                .child(notificationId)
                .setValue(notificationData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("NotificationUtils", "Notification saved successfully!");
                    Toast.makeText(context, "Notification saved!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("NotificationUtils", "Failed to save notification", e);
                    Toast.makeText(context, "Failed to save notification: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
    public static void getNotifications(Context context, NotificationsCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(context, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        FirebaseDatabase.getInstance().getReference("Registered User")
                .child(userId)
                .child("notifications") // Use "notifications" here
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    ArrayList<String> notifications = new ArrayList<>();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String title = snapshot.child("title").getValue(String.class);
                            String content = snapshot.child("content").getValue(String.class);
                            String timestamp = snapshot.child("timestamp").getValue(String.class);
                            if (title != null && content != null && timestamp != null) {
                                notifications.add(title + ": " + content + "\nTime: " + timestamp);
                            }
                        }
                    }
                    callback.onNotificationsFetched(notifications);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to fetch notifications!", Toast.LENGTH_SHORT).show());
    }



    public static void clearNotifications(Context context) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(context, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        FirebaseDatabase.getInstance().getReference("Registered User")
                .child(userId)
                .child("notifications") // Target only "notifications" node
                .removeValue()
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Notifications cleared!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to clear notifications!", Toast.LENGTH_SHORT).show());
    }



    public static void clearBadge(Context context) {
    }

    public interface NotificationsCallback {
        void onNotificationsFetched(ArrayList<String> notifications);
    }
}
