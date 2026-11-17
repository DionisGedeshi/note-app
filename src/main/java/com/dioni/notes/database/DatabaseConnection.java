package com.dioni.notes.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private final String url;
    private final String user;
    private final String password;

    public DatabaseConnection() {
        this(
                System.getenv("NOTES_DB_URL"),
                System.getenv("NOTES_DB_USER"),
                System.getenv("NOTES_DB_PASSWORD")
        );
    }

    public DatabaseConnection(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        validateConfiguration();

        return DriverManager.getConnection(url, user, password);
    }

    private void validateConfiguration() {
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("NOTES_DB_URL is not configured.");
        }

        if (user == null || user.isBlank()) {
            throw new IllegalStateException("NOTES_DB_USER is not configured.");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalStateException("NOTES_DB_PASSWORD is not configured.");
        }
    }
}