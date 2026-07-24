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

        UUID noteId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");

        try {
            Note note = jdbcNoteRepository.findById(noteId);

            System.out.println("Note with ID \"" + note.getId() + "\" before deletion:");
            System.out.println("Title: " + note.getTitle());
            System.out.println("Content: " + note.getContent());
            System.out.println("Created at: " + note.getCreationTime());
            System.out.println("Updated at: " + note.getUpdateTime());
            System.out.println("Archived: " + note.isArchived());
            System.out.println();

            jdbcNoteRepository.deleteById(noteId);
            try {
                jdbcNoteRepository.findById(noteId);
            } catch (IllegalArgumentException exception) {
                System.err.println("Deletion verified: " + exception.getMessage());
            }
        } catch (SQLException exception) {
            System.err.println("Database operation failed: " + exception.getMessage());
        }
    }
}