package com.example.aplikacionandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

    /**
     * NoteAdapter.java
     * <p>
     * A RecyclerView adapter for displaying a list of Note objects in a RecyclerView.
     * Provides functionality for binding data to the views and handling click events on individual items.
     */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    Context context;
    ArrayList<Note> arrayList;
    OnItemClickListener onItemClickListener;

    /**
     * Constructor for NoteAdapter.
     *
     * @param context   The context in which the adapter is used.
     * @param arrayList The list of Note objects to display.
     */
    public NoteAdapter(Context context, ArrayList<Note> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    /**
     * Inflates the layout for individual list items.
     *
     * @param parent   The parent ViewGroup.
     * @param viewType The type of view.
     * @return A new ViewHolder containing the inflated view.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_list_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds data from a Note object to the views in the ViewHolder.
     *
     * @param holder   The ViewHolder containing the views to bind data to.
     * @param position The position of the Note in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(arrayList.get(position).getTitle());
        holder.subtitle.setText(arrayList.get(position).getContent());
        holder.timestamp.setText(arrayList.get(position).getTimestamp());
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(arrayList.get(position)));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle, timestamp;

        /**
         * Constructor for ViewHolder.
         *
         * @param itemView The view representing an individual list item.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.list_item_title);
            subtitle = itemView.findViewById(R.id.list_item_subtitle);
            timestamp = itemView.findViewById(R.id.list_item_timestamp); // Lidhu me komponentën e re në layout
        }
    }

    /**
     * Returns the number of items in the list.
     *
     * @return The size of the Note list.
     */
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    /**
     * Sets the click listener for individual items in the RecyclerView.
     *
     * @param onItemClickListener The listener to handle click events.
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * Interface for handling click events on individual items.
     */
    public interface OnItemClickListener {
        void onClick(Note note);
    }
}