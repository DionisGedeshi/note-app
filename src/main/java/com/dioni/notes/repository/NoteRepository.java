package com.dioni.notes.repository;

import com.dioni.notes.model.Note;
import java.util.UUID;
import java.util.List;

public interface NoteRepository {
    void save(Note note);
    Note findById(UUID id);
    List<Note> findAll();
    int count();
}