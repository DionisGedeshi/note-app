package com.dioni.notes;

import com.dioni.notes.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionCheck {
    static void main(String[] args) {
        DatabaseConnection databaseConnection = new DatabaseConnection();

        try (Connection connection = databaseConnection.getConnection()) {
            boolean valid = connection.isValid(2);

            System.out.println("Database connection valid: " + valid);
            System.out.println("Connected database: " + connection.getCatalog());
        } catch (SQLException exception) {
            System.err.println("Could not connect to PostgreSQL.");

            System.err.println(exception.getMessage());
        }
    }
}