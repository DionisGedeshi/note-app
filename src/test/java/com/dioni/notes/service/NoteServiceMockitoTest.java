package com.dioni.notes.service;

import com.dioni.notes.exception.RepositoryException;
import com.dioni.notes.model.Note;
import com.dioni.notes.repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NoteServiceMockitoTest {
    @Mock
    private NoteRepository repository;

    @InjectMocks
    private NoteService service;

    @Test
    void shouldRetrieveNote() {
        UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
        String title = "New note title";
        String content = "New note content";
        LocalDateTime timestamp = LocalDateTime.now();
        boolean archived = false;

        Note note = new Note(id, title, content, timestamp, timestamp, archived);

        when(repository.findById(id)).thenReturn(note);

        Note result = service.getNoteById(id);

        assertSame(note, result);
        verify(repository).findById(id);
    }

    @Test
    void shouldCreateAndSaveNote() {
        Note note = service.createNote("Mockito title", "Mockito content");

        ArgumentCaptor<Note> captor = ArgumentCaptor.forClass(Note.class);

        verify(repository).save(captor.capture());

        Note capturedNote = captor.getValue();

        assertEquals("Mockito title", capturedNote.getTitle());
        assertEquals("Mockito content", capturedNote.getContent());
        assertNotNull(capturedNote.getId());
        assertNotNull(capturedNote.getCreationTime());
        assertNotNull(capturedNote.getUpdateTime());
        assertFalse(capturedNote.isArchived());
        assertSame(note, capturedNote);
    }

    @Test
    void shouldUpdateAndPersistNote() {
        UUID id = UUID.randomUUID();
        String title = "Mockito new title";
        String content = "Mockito new content";
        LocalDateTime timestamp = LocalDateTime.now().minusMinutes(1);
        boolean archived = false;

        Note note = new Note(id, title, content, timestamp, timestamp, archived);

        when(repository.findById(id)).thenReturn(note);

        UUID originalId = note.getId();
        LocalDateTime originalCreationTime = note.getCreationTime();
        LocalDateTime originalUpdateTime = note.getUpdateTime();

        Note updatedNote = service.updateNote(id, "New title", "New content");

        ArgumentCaptor<Note> captor = ArgumentCaptor.forClass(Note.class);

        InOrder order = inOrder(repository);

        order.verify(repository).findById(id);
        order.verify(repository).update(captor.capture());

        Note capturedNote = captor.getValue();

        assertEquals("New title", capturedNote.getTitle());
        assertEquals("New content", capturedNote.getContent());
        assertEquals(originalId, capturedNote.getId());
        assertEquals(originalCreationTime, capturedNote.getCreationTime());
        assertTrue(capturedNote.getUpdateTime().isAfter(originalUpdateTime));
        assertSame(updatedNote, capturedNote);
    }

    @Test
    void shouldDeleteNote() {
        UUID id = UUID.randomUUID();

        service.deleteNote(id);

        verify(repository).deleteById(id);
    }

    @Test
    void shouldFailDeletion() {
        UUID id = UUID.randomUUID();

        doThrow(new RepositoryException("Deletion failed")).when(repository).deleteById(id);

        assertThrows(RepositoryException.class, () -> service.deleteNote(id));
        verify(repository).deleteById(id);
    }

    @Test
    void shouldTestInvalidNoteCreation() {
        assertThrows(IllegalArgumentException.class, () -> service.createNote("", "Content"));

        verifyNoInteractions(repository);
    }

    @Test
    void shouldCreateNote() {
        service.createNote("Matched title", "Matched content");

        verify(repository).save(argThat(note -> note != null && note.getTitle().equals("Matched title") && note.getContent().equals("Matched content") && !note.isArchived()));

        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnDifferentNotesOnConsecutiveReads() {
        UUID id = UUID.randomUUID();
        LocalDateTime timestamp = LocalDateTime.now().minusMinutes(1);

        Note firstNote = new Note(id, "First version", "Content 1", timestamp, timestamp, false);
        Note secondNote = new Note(id, "Second version", "Content 2", timestamp, timestamp, false);

        when(repository.findById(id)).thenReturn(firstNote, secondNote);

        Note firstResult = service.getNoteById(id);
        Note secondResult = service.getNoteById(id);

        assertEquals("First version", firstResult.getTitle());
        assertEquals("Second version", secondResult.getTitle());
        verify(repository, times(2)).findById(id);
    }

    @Test
    void shouldReturnNoteMatchingRequestedId() {
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();

        when(repository.findById(any(UUID.class)))
                .thenAnswer(invocation -> {
                    UUID requestedId =
                            invocation.getArgument(0);

                    LocalDateTime timestamp =
                            LocalDateTime.now();

                    return new Note(requestedId,"Dynamic title", "Dynamic content", timestamp, timestamp, false);
                });

        Note firstResult = service.getNoteById(firstId);

        Note secondResult = service.getNoteById(secondId);

        assertEquals(firstId, firstResult.getId());
        assertEquals(secondId, secondResult.getId());

        assertNotSame(firstResult, secondResult);

        verify(repository, times(2)).findById(any(UUID.class));
    }
}