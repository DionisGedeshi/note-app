package com.dioni.notes.repository;

import com.dioni.notes.model.Note;
import java.util.UUID;
import java.util.List;

public interface NoteRepository {
    void save(Note note);
    Note findById(UUID id);
    List<Note> findAll();
    void update(Note note);
    void deleteById(UUID id);
    int count();
}