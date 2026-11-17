package com.dioni.notes.repository;

import com.dioni.notes.model.Note;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
        return notes.values().stream()
                .sorted(Comparator.comparing(Note::getUpdateTime).reversed())
                .toList();
    }

    @Override
    public List<Note> findByArchived(boolean archived) {
        return notes.values().stream()
                .filter(note -> note.isArchived() == archived)
                .sorted(Comparator.comparing(Note::getUpdateTime).reversed())
                .toList();
    }

    @Override
    public List<Note> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }

        String query = keyword.toLowerCase(Locale.ROOT);

        return notes.values().stream()
                .filter(note -> note.getTitle().toLowerCase(Locale.ROOT).contains(query)
                    || note.getContent().toLowerCase(Locale.ROOT).contains(query))
                .sorted(Comparator.comparing(Note::getUpdateTime).reversed())
                .toList();
    }

    @Override
    public void update(Note note) {
        if (note == null) {
            throw new IllegalArgumentException("Note cannot be null.");
        }

        if (!notes.containsKey(note.getId())) {
            throw new IllegalArgumentException("Note not found with ID: " + note.getId());
        }

        notes.put(note.getId(), note);
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null.");
        }

        if (notes.remove(id) == null) {
            throw new IllegalArgumentException("Note not found with ID: " + id);
        }
    }

    @Override
    public int count() {
        return notes.size();
    }
}