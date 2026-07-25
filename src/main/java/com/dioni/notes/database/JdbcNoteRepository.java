package com.dioni.notes.database;

import com.dioni.notes.exception.RepositoryException;
import com.dioni.notes.model.Note;
import com.dioni.notes.repository.NoteRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JdbcNoteRepository implements NoteRepository {
    private final DatabaseConnection databaseConnection;

    public JdbcNoteRepository(DatabaseConnection databaseConnection) {
        if (databaseConnection == null) {
            throw new IllegalArgumentException("Databace connection cannot be null.");
        }

        this.databaseConnection = databaseConnection;
    }

    private Note mapRowToNote(ResultSet resultSet) throws SQLException {
        UUID id = resultSet.getObject("id", UUID.class);
        String title = resultSet.getString("title");
        String content = resultSet.getString("content");
        LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
        LocalDateTime updatedAt = resultSet.getTimestamp("updated_at").toLocalDateTime();
        boolean archived = resultSet.getBoolean("archived");

        Note note = new Note(id, title, content, createdAt, updatedAt, archived);

        return note;
    }

    @Override
    public Note findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Note ID cannot be null");
        }

        String sql = "SELECT id, title, content, created_at, updated_at, archived FROM notes WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToNote(resultSet);
                }

                throw new IllegalArgumentException("Note ID not found.\nID: " + id);
            }
        } catch (SQLException exception) {
            throw new RepositoryException("Could not find note with ID: " + id, exception);
        }
    }

    @Override
    public List<Note> findAll() {
        String sql = "SELECT id, title, content, created_at, updated_at, archived FROM notes ORDER BY updated_at DESC";

        try (Connection connection = databaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            try(ResultSet resultSet = statement.executeQuery()) {
                List<Note> notes = new ArrayList<>();

                while (resultSet.next()) {
                    Note note = mapRowToNote(resultSet);
                    notes.add(note);
                }

                return notes;
            }
        } catch (SQLException exception) {
            throw new RepositoryException("Could not find all notes: ", exception);
        }
    }

    @Override
    public void save(Note note) {
        if (note == null) {
            throw new IllegalArgumentException("Note cannot be null.");
        }

        String sql = "INSERT INTO notes" +
                "(id, title, content, created_at, updated_at, archived)" +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = databaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setObject(1, note.getId());
            statement.setString(2, note.getTitle());
            statement.setString(3, note.getContent());
            statement.setTimestamp(4, Timestamp.valueOf(note.getCreationTime()));
            statement.setTimestamp(5, Timestamp.valueOf(note.getUpdateTime()));
            statement.setBoolean(6, note.isArchived());

            int affectedRows = statement.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException("Expected 1 inserted row, but affected: " + affectedRows);
            }
        } catch (SQLException exception) {
            throw new RepositoryException("Could not save note with ID: " + note.getId(), exception);
        }
    }

    @Override
    public void update(Note note) {
        if (note == null) {
            throw new IllegalArgumentException("Note cannot be null.");
        }

        String sql = "UPDATE notes SET title = ?, content = ?, updated_at = ? WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, note.getTitle());
            statement.setString(2, note.getContent());
            statement.setTimestamp(3, Timestamp.valueOf(note.getUpdateTime()));
            statement.setObject(4, note.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException("Expected 1 updated row, but affected: " + affectedRows);
            }
        } catch (SQLException exception) {
            throw new RepositoryException("Could not update note with ID: " + note.getId(), exception);
        }
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null.");
        }
        String sql = "DELETE FROM notes WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);

            int affectedRows = statement.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException("Expected 1 deleted row, but affected: " + affectedRows);
            }
        } catch (SQLException exception) {
            throw new RepositoryException("Could not delete note with ID: " + id, exception);
        }
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) AS count_notes FROM notes";

        try (Connection connection = databaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("count_notes");
                }

                throw new RepositoryException("Database returned no result when counting notes.");
            }
        } catch (SQLException exception) {
            throw new RepositoryException("Could not count notes: ", exception);
        }
    }
}