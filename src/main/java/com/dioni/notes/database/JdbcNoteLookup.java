package com.dioni.notes.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class JdbcNoteLookup {
    private final DatabaseConnection databaseConnection;

    public JdbcNoteLookup(DatabaseConnection databaseConnection) {
        if (databaseConnection == null) {
            throw new IllegalArgumentException("Databace connection cannot be null.");
        }

        this.databaseConnection = databaseConnection;
    }

    public String findTitleById(UUID id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("Note ID cannot be null");
        }

        String sql = "SELECT title FROM notes WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("title");
                }

                throw new IllegalArgumentException("Note ID not found.\nID: " + id);
            }
        }
    }
}