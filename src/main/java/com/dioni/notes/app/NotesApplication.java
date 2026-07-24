package com.dioni.notes.app;

import com.dioni.notes.database.DatabaseConnection;
import com.dioni.notes.database.JdbcNoteLookup;
import com.dioni.notes.model.Note;

import java.sql.SQLException;
import java.util.UUID;

public class NotesApplication {
    static void main(String[] args) {
        DatabaseConnection connection = new DatabaseConnection();
        JdbcNoteLookup jdbcNoteLookup = new JdbcNoteLookup(connection);

        UUID noteId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");

        try {
            Note note = jdbcNoteLookup.findById(noteId);
            System.out.println("Note ID: " + note.getId());
            System.out.println("Note title: " + note.getTitle());
            System.out.println("Note content: " + note.getContent());
            System.out.println("Note creation time: " + note.getCreationTime());
            System.out.println("Latest note modification time: " + note.getUpdateTime());
            System.out.println("Note archived status: " + note.isArchived());
        } catch (SQLException | IllegalArgumentException exception) {
            System.err.println("Could not retrieve note: " + exception.getMessage());
        }
    }
}
