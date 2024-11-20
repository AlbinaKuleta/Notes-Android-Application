package com.example.aplikacionandroid;

public class Note {
    private String key;
    private String title;
    private String content;

    // Konstruktor bosh (i nevojshëm për Firebase)
    public Note() {
    }

    // Konstruktor për inicializimin e një shënimi
    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // Getter dhe Setter për titullin
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter dhe Setter për përmbajtjen
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Getter dhe Setter për çelësin unik të Firebase
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
