package com.example.aplikacionandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.aplikacionandroid.NotificationsActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class NotificationUtils {
    private static final String PREFS_NAME = "notifications_prefs";
    private static final String NOTIFICATIONS_KEY = "notifications";

    public static void saveNotification(Context context, String notification) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> notifications = prefs.getStringSet(NOTIFICATIONS_KEY, new HashSet<>());

        String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        notifications.add(notification + "\nTime: " + timestamp);
        prefs.edit().putStringSet(NOTIFICATIONS_KEY, notifications).apply();
    }

    public static ArrayList<String> getNotifications(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> notifications = prefs.getStringSet(NOTIFICATIONS_KEY, new HashSet<>());

        return new ArrayList<>(notifications);
    }

    public static void clearNotifications(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(NOTIFICATIONS_KEY).apply();
    }
    public static void clearBadge(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt("unread_count", 0).apply();
    }

}
