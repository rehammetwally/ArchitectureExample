package com.rehammetwally.architectureexample.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rehammetwally.architectureexample.R;
import com.rehammetwally.architectureexample.adapters.NotesAdapter;
import com.rehammetwally.architectureexample.data.entities.Note;
import com.rehammetwally.architectureexample.viewmodels.NotesViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private NotesViewModel notesViewModel;
    private RecyclerView notesRecyclerView;
    private FloatingActionButton fab;
    private static final String TAG = "MainActivity";
    public static final int ADD_NOTE_REQUEST_CODE = 1;
    public static final int UPDATE_NOTE_REQUEST_CODE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notesRecyclerView.setHasFixedSize(true);
        final NotesAdapter adapter = new NotesAdapter();
        notesRecyclerView.setAdapter(adapter);


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);

        notesViewModel = ViewModelProviders.of(this).get(NotesViewModel.class);
        notesViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.submitList(notes);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                notesViewModel.delete(adapter.getNoteAtPosition(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Note deleted!!", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(notesRecyclerView);

        adapter.setOnItemClickListener(new NotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                intent.putExtra(AddNoteActivity.EXTRA_ID, note.getId());
                intent.putExtra(AddNoteActivity.EXTRA_TITLE, note.getTitle());
                intent.putExtra(AddNoteActivity.EXTRA_DESCRIPTION, note.getDescription());
                intent.putExtra(AddNoteActivity.EXTRA_PRIORITY, note.getPriority());
                startActivityForResult(intent, UPDATE_NOTE_REQUEST_CODE);
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Intent intent = new Intent(this, AddNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String title = data.getStringExtra(AddNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddNoteActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddNoteActivity.EXTRA_PRIORITY, 0);
            if (resultCode == RESULT_OK && requestCode == ADD_NOTE_REQUEST_CODE) {
                notesViewModel.insert(new Note(title, description, priority));
                Toast.makeText(this, "Note saved success", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_OK && requestCode == UPDATE_NOTE_REQUEST_CODE) {
                int id=data.getIntExtra(AddNoteActivity.EXTRA_ID,-1);
                if (id == -1) {
                    Toast.makeText(this, "Note can't be updated !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Note note=new Note(title, description, priority);
                note.setId(id);
                notesViewModel.update(note);
                Toast.makeText(this, "Note updated success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Note not saved !!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteAllNotes:
                notesViewModel.deleteAllNotes();
                Toast.makeText(this, "All notes deleted!!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
