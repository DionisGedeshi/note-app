package com.dioni.notes.service;

import com.dioni.notes.model.Note;
import com.dioni.notes.repository.InMemoryNoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NoteServiceTest {
    private InMemoryNoteRepository repository;
    private NoteService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryNoteRepository();
        service = new NoteService(repository);
    }

    @Test
    void createNoteTest() {
        Note note = service.createNote("Title", "Content");

        assertNotNull(note);
        assertNotNull(note.getTitle());
        assertNotNull(note.getContent());
        assertEquals(1, service.countNotes());
    }

    @Test
    void retrieveNoteTest() {
        UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
        String title = "New note title";
        String content = "New note content";
        LocalDateTime timestamp = LocalDateTime.now();
        LocalDateTime createdAt = timestamp;
        LocalDateTime updatedAt = timestamp;
        boolean archived = true;

        Note note = new Note(id, title, content, createdAt, updatedAt, archived);
        repository.save(note);

        Note retrievedNote = service.getNoteById(note.getId());

        assertEquals(note.getId(), retrievedNote.getId());
    }

    @Test
    void updateNoteTest() {
        UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
        String title = "New note title";
        String content = "New note content";
        LocalDateTime createdAt = LocalDateTime.of(2026, 07, 21, 21, 00, 01);
        LocalDateTime updatedAt = LocalDateTime.of(2026, 07, 22, 21, 00, 01);
        boolean archived = true;

        Note note = new Note(id, title, content, createdAt, updatedAt, archived);
        repository.save(note);

        UUID originalId = note.getId();
        String originalTitle = note.getTitle();
        String originalContent = note.getContent();
        LocalDateTime originalCreatedAt = note.getCreationTime();
        LocalDateTime originalUpdatedAt = note.getUpdateTime();
        boolean originalArchived = note.isArchived();

        Note updatedNote = service.updateNote(note.getId(), "This note has a brand new title", "This note has some brand new content");

        UUID updatedId = updatedNote.getId();
        String updatedTitle = updatedNote.getTitle();
        String updatedContent = updatedNote.getContent();
        LocalDateTime updatedCreatedAt = updatedNote.getCreationTime();
        LocalDateTime updatedUpdatedAt = updatedNote.getUpdateTime();
        boolean updatedArchived = updatedNote.isArchived();

        assertTrue(updatedNote.getUpdateTime().isAfter(originalUpdatedAt));
        assertEquals(originalId, updatedId);
        assertTrue(updatedNote.getCreationTime().isEqual(originalCreatedAt));

        assertEquals("This note has a brand new title", updatedTitle);
        assertEquals("This note has some brand new content", updatedContent);

        assertEquals(originalId, updatedId);
        assertEquals(originalCreatedAt, updatedCreatedAt);
        assertEquals(originalArchived, updatedArchived);
        assertTrue(updatedUpdatedAt.isAfter(originalUpdatedAt));
    }

    @Test
    void deleteNoteTest() {
        UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
        String title = "New note title";
        String content = "New note content";
        LocalDateTime timestamp = LocalDateTime.now();
        LocalDateTime createdAt = timestamp;
        LocalDateTime updatedAt = timestamp;
        boolean archived = true;

        Note note = new Note(id, title, content, createdAt, updatedAt, archived);

        repository.save(note);

        service.deleteNote(id);

        assertEquals(0, service.countNotes());
        assertThrows(IllegalArgumentException.class, () -> service.getNoteById(id));
    }

    @Test
    void verifyCountingAndListingNotes() {
        Note firstNote = service.createNote("Title 1", "Content 1");
        Note secondNote = service.createNote("Title 2", "Content 2");
        Note thirdNote = service.createNote("Title 3", "Content 3");

        List<Note> notes = service.getAllNotes();

        assertEquals(3, service.countNotes());
        assertEquals(3, notes.size());

        boolean containsFirstNote = notes.stream()
                .anyMatch(note ->
                        note.getId().equals(firstNote.getId())
                );

        assertTrue(containsFirstNote);

        boolean containsSecondNote = notes.stream()
                .anyMatch(note ->
                        note.getId().equals(secondNote.getId())
                );

        assertTrue(containsSecondNote);

        boolean containsThirdNote = notes.stream()
                .anyMatch(note ->
                        note.getId().equals(thirdNote.getId())
                );

        assertTrue(containsThirdNote);
    }

    @Test
    void handlesNullContentAndArchivingLifecycle() {
        Note note = service.createNote("Archive Me", null);
        assertEquals("", note.getContent());
        assertFalse(note.isArchived());
        assertEquals(1, service.getActiveNotes().size());
        assertEquals(0, service.getArchivedNotes().size());

        Note archived = service.archiveNote(note.getId());
        assertTrue(archived.isArchived());
        assertEquals(0, service.getActiveNotes().size());
        assertEquals(1, service.getArchivedNotes().size());

        Note unarchived = service.unarchiveNote(note.getId());
        assertFalse(unarchived.isArchived());
        assertEquals(1, service.getActiveNotes().size());
    }

    @Test
    void searchesNotesCaseInsensitively() {
        service.createNote("Java Concurrency", "Virtual threads in JDK 21");
        service.createNote("Arch Linux Setup", "Hyprland and HyDE config");

        List<Note> javaMatches = service.searchNotes("java");
        assertEquals(1, javaMatches.size());
        assertEquals("Java Concurrency", javaMatches.getFirst().getTitle());

        List<Note> contentMatches = service.searchNotes("hyprland");
        assertEquals(1, contentMatches.size());
        assertEquals("Arch Linux Setup", contentMatches.getFirst().getTitle());
    }
}