package com.example.aplikacionandroid;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Note.java
 * <p>
 * Represents a note object in the One Note Application.
 * Each note includes a title, content, a unique key, and a timestamp indicating when the note was created or last updated.
 */

public class Note {
    private String key;
    private String title;
    private String content;
    private String timestamp;

    /**
     * Default constructor for creating an empty Note object.
     */
    public Note() {
    }

    /**
     * Constructor for creating a Note with a title and content.
     * Automatically sets the timestamp to the current date and time.
     *
     * @param title   The title of the note.
     * @param content The content of the note.
     */
    public Note(String title, String content) {
        this.title = title;
        this.content = content;

        this.timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    /**
     * Gets the title of the note.
     *
     * @return The title of the note.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the note.
     *
     * @param title The title to set for the note.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the content of the note.
     *
     * @return The content of the note.
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the note.
     *
     * @param content The content to set for the note.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the unique key of the note.
     *
     * @return The unique key of the note.
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the unique key of the note.
     *
     * @param key The unique key to set for the note.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the timestamp of the note.
     *
     * @return The timestamp of the note.
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of the note.
     *
     * @param timestamp The timestamp to set for the note.
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
