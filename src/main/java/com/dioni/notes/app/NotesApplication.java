package com.dioni.notes.app;

import com.dioni.notes.database.DatabaseConnection;
import com.dioni.notes.database.JdbcNoteRepository;
import com.dioni.notes.model.Note;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class NotesApplication {
    static void main(String[] args) {
        DatabaseConnection connection = new DatabaseConnection();
        JdbcNoteRepository jdbcNoteRepository = new JdbcNoteRepository(connection);

        Note newNote = new Note("Random note title.", "Random note content");

        try {
            jdbcNoteRepository.save(newNote);

            UUID newNoteId = newNote.getId();

            Note retrievedNote = jdbcNoteRepository.findById(newNoteId);
            System.out.println("ID of the new note: " + retrievedNote.getId());
            System.out.println("Title: " + retrievedNote.getTitle());
            System.out.println("Content: " + retrievedNote.getContent());
        } catch (SQLException exception) {
            System.err.println("Could not save note: " + exception.getMessage());
        }

        try {
            List<Note> notes = jdbcNoteRepository.findAll();

            for (Note note : notes) {
                System.out.println("----------------------------------------");
                System.out.println(note.getTitle());
                System.out.println(note.getUpdateTime());
                System.out.println(note.isArchived());
                System.out.println();
            }

            System.out.println("Total number of notes found: " + notes.size());
        } catch (SQLException | IllegalArgumentException exception) {
            System.err.println("Count not retrieve notes: " + exception.getMessage());
        }
    }
}