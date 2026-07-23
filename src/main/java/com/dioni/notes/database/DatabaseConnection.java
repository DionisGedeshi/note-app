package com.dioni.notes.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = System.getenv("NOTES_DB_URL");
    private static final String USER = System.getenv("NOTES_DB_USER");
    private static final String PASSWORD = System.getenv("NOTES_DB_PASSWORD");

    public Connection getConnection() throws SQLException {
        validateConfiguration();

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private void validateConfiguration() {
        if (URL == null || URL.isBlank()) {
            throw new IllegalStateException("NOTES_DB_URL is not configured.");
        }

        if (USER == null || USER.isBlank()) {
            throw new IllegalStateException("NOTES_DB_USER is not configured.");
        }

        if (PASSWORD == null || PASSWORD.isBlank()) {
            throw new IllegalStateException("NOTES_DB_PASSWORD is not configured.");
        }
    }
}