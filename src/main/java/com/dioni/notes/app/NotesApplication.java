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

        /*
        Retrieve an existing note --Done
        Print its original values --Done
        Edit its title and content --Done
        Call repository.update(note) --Done
        Retrieve it again --Done
        Print the new values --Done
         */

        UUID noteId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");

        try {
            Note note = jdbcNoteRepository.findById(noteId);

            System.out.println("Note with ID \"" + note.getId() + "\" before update:");
            System.out.println("Title: " + note.getTitle());
            System.out.println("Content: " + note.getContent());
            System.out.println("Created at: " + note.getCreationTime());
            System.out.println("Updated at: " + note.getUpdateTime());
            System.out.println("Archived: " + note.isArchived());
            System.out.println();

            note.updateNote("This is a new title", "This is the new content");
            jdbcNoteRepository.update(note);

            Note updatedNote = jdbcNoteRepository.findById(noteId);

            System.out.println("Note with ID \"" + updatedNote.getId() + "\" after update: ");
            System.out.println("Title: " + updatedNote.getTitle());
            System.out.println("Content: " + updatedNote.getContent());
            System.out.println("Created at: " + updatedNote.getCreationTime());
            System.out.println("Updated at: " + updatedNote.getUpdateTime());
            System.out.println("Archived: " + updatedNote.isArchived());
        } catch (SQLException | IllegalArgumentException exception) {
            System.err.println("Could not retrieve note: " + exception.getMessage());
        }
    }
}