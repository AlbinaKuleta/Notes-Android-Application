package com.example.aplikacionandroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * This class represents the adapter for the RecyclerView used in NotificationsActivity.
 * It binds notification data to the views in the notification list layout.
 */

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private ArrayList<String> notifications;

    /**
     * Constructor to initialize the adapter with a list of notifications.
     *
     * @param notifications The list of notifications to be displayed in the RecyclerView.
     */
    public NotificationsAdapter(ArrayList<String> notifications) {
        this.notifications = notifications;
    }

    /**
     * Inflates the layout for each notification item in the RecyclerView.
     *
     * @param parent   The parent ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder instance containing the inflated layout.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds data from the notifications list to the views in the ViewHolder.
     *
     * @param holder   The ViewHolder which should be updated to display the notification at the given position.
     * @param position The position of the notification in the data list.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.notificationText.setText(notifications.get(position));
    }

    /**
     * Returns the total number of notifications in the list.
     *
     * @return The size of the notifications list.
     */
    @Override
    public int getItemCount() {
        return notifications.size();
    }

    /**
     * ViewHolder class that holds references to the views for each notification item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView notificationText;

        /**
         * Constructor for ViewHolder, initializes the views for a notification item.
         *
         * @param itemView The View representing an individual notification item.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationText = itemView.findViewById(R.id.notification_text);
        }
    }
}
