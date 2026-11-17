package com.dioni.notes.database;

import com.dioni.notes.model.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers(disabledWithoutDocker = true)
class JdbcNoteRepositoryTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    private JdbcNoteRepository repository;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseConnection databaseConnection = new DatabaseConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

        repository = new JdbcNoteRepository(databaseConnection);
        repository.initializeSchema();

        try (Connection conn = databaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("TRUNCATE TABLE notes");
        }
    }

    @Test
    void saveAndFindById_persistsAndRetrievesNote() {
        Note note = new Note("PostgreSQL Indexing", "Use GIN indexes for full-text search");
        repository.save(note);

        Note loaded = repository.findById(note.getId());

        assertNotNull(loaded);
        assertEquals("PostgreSQL Indexing", loaded.getTitle());
        assertEquals("Use GIN indexes for full-text search", loaded.getContent());
        assertFalse(loaded.isArchived());
    }

    @Test
    void update_modifiesExistingNoteInDatabase() {
        Note note = new Note("Draft Title", "Initial body");
        repository.save(note);

        note.updateNote("Final Architecture Notes", "Layered CLI + JDBC + Testcontainers");
        repository.update(note);

        Note updated = repository.findById(note.getId());
//        assertNotNull(updated);
        assertEquals("Final Architecture Notes", updated.getTitle());
        assertEquals("Layered CLI + JDBC + Testcontainers", updated.getContent());
    }

    @Test
    void searchByKeyword_matchesCaseInsensitiveInPostgres() {
        repository.save(new Note("PostgreSQL Indexing", "B-Tree and GIN indexes"));
        repository.save(new Note("Docker Compose", "Local container orchestration"));

        List<Note> results = repository.search("gin");

        assertEquals(1, results.size());
        assertEquals("PostgreSQL Indexing", results.getFirst().getTitle());
    }

    @Test
    void deleteById_removesNoteFromDatabase() {
        Note note = new Note("Temporary Note", "To be deleted");
        repository.save(note);

        repository.deleteById(note.getId());

        assertThrows(IllegalArgumentException.class, () -> repository.findById(note.getId()));
    }
}