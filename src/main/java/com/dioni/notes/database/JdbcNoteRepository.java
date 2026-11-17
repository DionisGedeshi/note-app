package com.dioni.notes.database;

import com.dioni.notes.exception.RepositoryException;
import com.dioni.notes.model.Note;
import com.dioni.notes.repository.NoteRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JdbcNoteRepository implements NoteRepository {
    private final DatabaseConnection databaseConnection;

    public JdbcNoteRepository(DatabaseConnection databaseConnection) {
        if (databaseConnection == null) {
            throw new IllegalArgumentException("Database connection cannot be null");
        }

        this.databaseConnection = databaseConnection;
    }

    public void initializeSchema() {
        String sql = """
                CREATE TABLE IF NOT EXISTS notes (
                    id UUID PRIMARY KEY,
                    title VARCHAR(200) NOT NULL,
                    content TEXT NOT NULL DEFAULT '',
                    created_at TIMESTAMP NOT NULL,
                    updated_at TIMESTAMP NOT NULL,
                    archived BOOLEAN NOT NULL DEFAULT false
                )
                """;

        try (Connection connection = databaseConnection.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException exception) {
            throw new RepositoryException("Could not initialize database schema.", exception);
        }
    }

    private Note mapRowToNote(ResultSet resultSet) throws SQLException {
        UUID id = resultSet.getObject("id", UUID.class);
        String title = resultSet.getString("title");
        String content = resultSet.getString("content");
        LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
        LocalDateTime updatedAt = resultSet.getTimestamp("updated_at").toLocalDateTime();
        boolean archived = resultSet.getBoolean("archived");

        return new Note(id, title, content, createdAt, updatedAt, archived);
    }

    @Override
    public Note findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Note ID cannot be null.");
        }

        String sql = "SELECT id, title, content, created_at, updated_at, archived FROM notes WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToNote(resultSet);
                }

                throw new IllegalArgumentException("Note not found with ID: " + id);
            }
        } catch (SQLException exception) {
            throw new RepositoryException("Could not find note with ID: " + id, exception);
        }
    }

    @Override
    public List<Note> findAll() {
        String sql = "SELECT id, title, content, created_at, updated_at, archived FROM notes ORDER BY updated_at DESC";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Note> notes = new ArrayList<>();

            while (resultSet.next()) {
                notes.add(mapRowToNote(resultSet));
            }

            return notes;
        } catch (SQLException exception) {
            throw new RepositoryException("Could not find all notes.", exception);
        }
    }

    @Override
    public List<Note> findByArchived(boolean archived) {
        String sql = "SELECT id, title, content, created_at, updated_at, archived FROM notes WHERE archived = ? ORDER BY updated_at DESC";

        try (Connection connection = databaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, archived);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Note> notes = new ArrayList<>();

                while (resultSet.next()) {
                    notes.add(mapRowToNote(resultSet));
                }

                return notes;
            }
        } catch (SQLException exception) {
            throw new RepositoryException("Could not filter notes by archived status: " + archived, exception);
        }
    }

    @Override
    public List<Note> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }

        String sql = """
                SELECT id, title, content, created_at, updated_at, archived
                FROM notes
                WHERE title ILIKE ? OR content ILIKE ?
                ORDER BY updated_at DESC
                """;

        String pattern = "%" + keyword.trim() + "%";

        try (Connection connection = databaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pattern);
            statement.setString(2, pattern);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Note> notes = new ArrayList<>();

                while (resultSet.next()) {
                    notes.add(mapRowToNote(resultSet));
                }

                return notes;
            }
        } catch (SQLException exception) {
            throw new RepositoryException("Count not search notes with keyword: " + keyword, exception);
        }
    }

    @Override
    public void save(Note note) {
        if (note == null) {
            throw new IllegalArgumentException("Note cannot be null.");
        }

        String sql = """
                INSERT INTO notes (id, title, content, created_at, updated_at, archived)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = databaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
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
            throw new RepositoryException("Count not save note with ID: " + note.getId(), exception);
        }
    }

    @Override
    public void update(Note note) {
        if (note == null) {
            throw new IllegalArgumentException("Note cannot be null.");
        }

        String sql = "UPDATE notes SET title = ?, content = ?, updated_at = ?, archived = ?, WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, note.getTitle());
            statement.setString(2, note.getContent());
            statement.setTimestamp(3, Timestamp.valueOf(note.getUpdateTime()));
            statement.setBoolean(4, note.isArchived());
            statement.setObject(5, note.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Expected 1 updated row, bt affected: " + affectedRows);
            }
        } catch (SQLException exception) {
            throw new RepositoryException("Count not update note with ID: " + note.getId(), exception);
        }
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null.");
        }

        String sql = "DELETE FROM notes WHERE id = ?";

        try (Connection connection = databaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);

            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new SQLException("Excpected 1 deleted row, but affected: " + affectedRows);
            }
        } catch (SQLException exception) {
            throw new RepositoryException("Could not delete note with ID: " + id, exception);
        }
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) AS count_notes FROM notes";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("count_notes");
            }

            throw new RepositoryException("Database returned no result when counting notes.");
        } catch (SQLException exception) {
            throw new RepositoryException("Could not count notes.", exception);
        }
    }
}