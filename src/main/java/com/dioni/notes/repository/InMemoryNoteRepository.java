package com.dioni.notes.repository;
import com.dioni.notes.model.Note;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InMemoryNoteRepository implements NoteRepository {
    private final Map<UUID, Note> notes = new HashMap<>();

    @Override
    public void save(Note note) {
        if (note == null) {
            throw new IllegalArgumentException("Note cannot be null.");
        }

        notes.put(note.getId(), note);
    }

    @Override
    public Note findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Note ID cannot be null.");
        }

        Note note = notes.get(id);

        if (note == null) {
            throw new IllegalArgumentException("Note not found with ID: " + id);
        }

        return note;
    }

    @Override
    public List<Note> findAll() {
        return new ArrayList<>(notes.values());
    }

    @Override
    public int count() {
        return notes.size();
    }
}
