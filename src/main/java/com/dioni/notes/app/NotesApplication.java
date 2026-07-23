package com.dioni.notes.app;

import com.dioni.notes.database.DatabaseConnection;
import com.dioni.notes.database.JdbcNoteLookup;

import java.sql.SQLException;
import java.util.UUID;

public class NotesApplication {
    static void main(String[] args) {
        DatabaseConnection connection = new DatabaseConnection();
        JdbcNoteLookup jdbcNoteLookup = new JdbcNoteLookup(connection);

        // Note from dev. You can test the catch statement by giving a random UUID(e.g. change the last 0 to 1) :)
        UUID noteId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");

        try {
            String title = jdbcNoteLookup.findTitleById(noteId);
            System.out.println("Note title: " + title);
        } catch (SQLException | IllegalArgumentException exception) {
            System.err.println("Could not retrieve note: " + exception.getMessage());
        }
    }
}
