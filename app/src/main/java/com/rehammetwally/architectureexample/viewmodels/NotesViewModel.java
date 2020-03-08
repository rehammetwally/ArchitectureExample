package com.rehammetwally.architectureexample.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rehammetwally.architectureexample.data.entities.Note;
import com.rehammetwally.architectureexample.repositories.NotesRepository;

import java.util.List;

public class NotesViewModel extends AndroidViewModel {
    private NotesRepository notesRepository;
    private LiveData<List<Note>> allNotes;

    public NotesViewModel(@NonNull Application application) {
        super(application);
        notesRepository = new NotesRepository(application);
        allNotes = notesRepository.getAllNotes();
    }

    public void insert(Note note) {
        notesRepository.insert(note);
    }

    public void update(Note note) {
        notesRepository.update(note);
    }

    public void delete(Note note) {
        notesRepository.delete(note);
    }

    public void deleteAllNotes() {
        notesRepository.deleteAllNotes();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }


}
