package com.example.aplikacionandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;

public class NotesActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseDatabase database;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        FirebaseApp.initializeApp(this);
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser currentUser = authProfile.getCurrentUser();

        // Kontrollo nëse përdoruesi është autentifikuar
        if (currentUser == null) {
            Toast.makeText(this, "Please log in first!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Merr ID-në unike të përdoruesit
        userId = currentUser.getUid();
        database = FirebaseDatabase.getInstance();

        FloatingActionButton add = findViewById(R.id.addNote);
        TextView empty = findViewById(R.id.empty);
        RecyclerView recyclerView = findViewById(R.id.recycler);

        add.setOnClickListener(view -> openAddNoteDialog());

        // Lexo shënimet e përdoruesit
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
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Failed to add note!", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateNoteInDatabase(String key, Note note) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Updating note...");
        dialog.show();

        database.getReference("Registered User").child(userId).child("notes").child(key)
                .setValue(note)
                .addOnSuccessListener(unused -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Note updated successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Failed to update note!", Toast.LENGTH_SHORT).show();
                });
    }

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

    //Creating ActionBar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    //When any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.menu_refresh){
            //refresh activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        }else if(id == R.id.menu_notes){
            Intent intent = new Intent(NotesActivity.this, NotesActivity.class);
            startActivity(intent);
            finish();
        }else if(id == R.id.menu_update_profile){
            Intent intent = new Intent(NotesActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }else if(id == R.id.menu_update_email){
            Intent intent = new Intent(NotesActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.menu_settings){
            Toast.makeText(NotesActivity.this, "menu_settings",Toast.LENGTH_SHORT).show();
        }else if(id == R.id.menu_delete_profile){
            Intent intent = new Intent(NotesActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        }else if(id == R.id.menu_logout){
            authProfile.signOut();
            Toast.makeText(NotesActivity.this, "Logged Out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(NotesActivity.this, MainActivity.class);

            //clear stack to prevent user coming back to UserProfileActivity on pressing back button after logging out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); //close user profile activity
        }else{
            Toast.makeText(NotesActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();

        }
        return super.onOptionsItemSelected(item);
    }
}

