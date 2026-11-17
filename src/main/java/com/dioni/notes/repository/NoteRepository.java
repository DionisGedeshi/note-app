package com.dioni.notes.repository;

import com.dioni.notes.model.Note;
import java.util.List;
import java.util.UUID;

public interface NoteRepository {
    void save(Note note);
    Note findById(UUID id);
    List<Note> findAll();
    List<Note> findByArchived(boolean archived);
    List<Note> search(String keyword);
    void update(Note note);
    void deleteById(UUID id);
    int count();
}