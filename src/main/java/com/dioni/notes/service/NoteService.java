package com.dioni.notes.service;

import com.dioni.notes.model.Note;
import com.dioni.notes.repository.NoteRepository;
import java.util.List;
import java.util.UUID;

public class NoteService {
    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        if (noteRepository == null) {
            throw new IllegalArgumentException("Note repository cannot be null");
        }

        this.noteRepository = noteRepository;
    }

    public Note createNote(String title, String content) {
        Note note = new Note(title, content);
        noteRepository.save(note);

        return note;
    }

    public Note getNoteById(UUID id) {
        return noteRepository.findById(id);
    }

    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    public List<Note> getActiveNotes() {
        return noteRepository.findByArchived(false);
    }

    public List<Note> getArchivedNotes() {
        return noteRepository.findByArchived(true);
    }

    public List<Note> searchNotes(String keyword) {
        return noteRepository.search(keyword);
    }

    public Note updateNote(UUID id, String newTitle, String newContent) {
        Note note = noteRepository.findById(id);
        note.updateNote(newTitle, newContent);
        noteRepository.update(note);

        return note;
    }

    public Note archiveNote(UUID id) {
        Note note = noteRepository.findById(id);
        note.archive();
        noteRepository.update(note);

        return note;
    }

    public Note unarchiveNote(UUID id) {
        Note note = noteRepository.findById(id);
        note.unarchive();
        noteRepository.update(note);

        return note;
    }

    public void deleteNote(UUID id) {
        noteRepository.deleteById(id);
    }

    public int countNotes() {
        return noteRepository.count();
    }
}