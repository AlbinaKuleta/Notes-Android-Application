package com.example.aplikacionandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NotificationUtils {
    private static final String PREFS_NAME = "notifications_prefs";
    private static final String NOTIFICATIONS_KEY = "notifications";

    public static void saveNotification(Context context, String notification) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> notifications = prefs.getStringSet(NOTIFICATIONS_KEY, new HashSet<>());

        notifications.add(notification);
        prefs.edit().putStringSet(NOTIFICATIONS_KEY, notifications).apply();
    }

    public static ArrayList<String> getNotifications(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> notifications = prefs.getStringSet(NOTIFICATIONS_KEY, new HashSet<>());

        return new ArrayList<>(notifications);
    }
}
