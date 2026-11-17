package com.dioni.notes.app;

import com.dioni.notes.database.DatabaseConnection;
import com.dioni.notes.database.JdbcNoteRepository;
import com.dioni.notes.model.Note;
import com.dioni.notes.repository.InMemoryNoteRepository;
import com.dioni.notes.repository.NoteRepository;
import com.dioni.notes.service.NoteService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class NotesApplication {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static void main(String[] args) {
        NoteRepository repository = initializeRepository();
        NoteService noteService = new NoteService(repository);

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;

            while (running) {
                printMenu(noteService.countNotes());
                String choice = scanner.nextLine().trim();

                try {
                    switch (choice) {
                        case "1" -> createNote(scanner, noteService);
                        case "2" -> printNotes("Active Notes", noteService.getActiveNotes());
                        case "3" -> printNotes("Archived Notes", noteService.getArchivedNotes());
                        case "4" -> viewNote(scanner, noteService);
                        case "5" -> updateNote(scanner, noteService);
                        case "6" -> toggleArchive(scanner, noteService);
                        case "7" -> searchNotes(scanner, noteService);
                        case "8" -> deleteNote(scanner, noteService);
                        case "0" -> {
                            running = false;
                            System.out.println("Goodbye.");
                        }
                        default -> System.out.println("Invalid option. Choose 0-8.");
                    }
                } catch (RuntimeException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
        }
    }

    private static NoteRepository initializeRepository() {
        String dbUrl = System.getenv("NOTES_DB_URL");
        String dbUser = System.getenv("NOTES_DB_USER");
        String dbPassword = System.getenv("NOTES_DB_PASSWORD");

        boolean hasDbConfig = dbUrl != null && !dbUrl.isBlank()
                && dbUser != null && !dbUser.isBlank()
                && dbPassword != null && !dbPassword.isBlank();

        if (hasDbConfig) {
            try {
                JdbcNoteRepository jdbcRepository = new JdbcNoteRepository(new DatabaseConnection());
                jdbcRepository.initializeSchema();
                System.out.println("[Storage] Connected to PostgreSQL (" + dbUrl + ")");

                return jdbcRepository;
            } catch (RuntimeException ex) {
                System.out.println("[Storage] PostgreSQL unavailable (" + ex.getMessage() + ") - falling back to in-memory mode.");
            }
        } else {
            System.out.println("[Storage] PostgreSQL environment variables not fully set - running in-memory mode.");
        }

        return new InMemoryNoteRepository();
    }

    private static void printMenu(int totalNotes) {
        System.out.println("\n=== Notes Vault (" + totalNotes + " total) ===");
        System.out.println("1. Create note");
        System.out.println("2. List active notes");
        System.out.println("3. List archived notes");
        System.out.println("4. View note details");
        System.out.println("5. Update note");
        System.out.println("6. Archive / Unarchive note");
        System.out.println("7. Search notes");
        System.out.println("8. Delete note");
        System.out.println("0. Exit");
        System.out.print("Select option: ");
    }

    private static void createNote(Scanner scanner, NoteService service) {
        System.out.print("Title: ");
        String title = scanner.nextLine();

        System.out.print("Content: ");
        String content = scanner.nextLine();

        Note created = service.createNote(title, content);
        System.out.println("Created note [" + shortId(created.getId()) + "] \"" + created.getTitle() + "\"");
    }

    private static void viewNote(Scanner scanner, NoteService service) {
        Note note = resolveNote(scanner, service);

        System.out.println("\n----------------------------------------");
        System.out.println("ID:" + note.getId());
        System.out.println("Title:    " + note.getTitle());
        System.out.println("Status:   " + (note.isArchived() ? "Archived" : "Active"));
        System.out.println("Created:  " + note.getCreationTime().format(FORMATTER));
        System.out.println("Updated:  " + note.getUpdateTime().format(FORMATTER));
        System.out.println("Content:\n" + (note.getContent().isEmpty() ? "(empty)" : note.getContent()));
        System.out.println("\n----------------------------------------");
    }

    private static void updateNote(Scanner scanner, NoteService service) {
        Note existing = resolveNote(scanner, service);
        System.out.print("New title (leave blank to keep \"" + existing.getTitle() + "\"): ");
        String titleInput = scanner.nextLine();
        String newTitle = titleInput.isBlank() ? existing.getTitle() : titleInput;

        System.out.print("New content: ");
        String newContent = scanner.nextLine();

        Note updated = service.updateNote(existing.getId(), newTitle, newContent);
        System.out.println("Updated note [" + shortId(updated.getId()) + "]");
    }

    private static void toggleArchive(Scanner scanner, NoteService service) {
        Note existing = resolveNote(scanner, service);
        Note updated = existing.isArchived()
                ? service.unarchiveNote(existing.getId())
                : service.archiveNote(existing.getId());

        System.out.println("Note [" + shortId(updated.getId()) + "] is now " + (updated.isArchived() ? "ARCHIVED." : "ACTIVE."));
    }

    private static void searchNotes(Scanner scanner, NoteService service) {
        System.out.print("Search keyword: ");
        String keyword = scanner.nextLine();

        printNotes("Search Results for \"" + keyword + "\"", service.searchNotes(keyword));
    }

    private static void deleteNote(Scanner scanner, NoteService service) {
        Note existing = resolveNote(scanner, service);
        service.deleteNote(existing.getId());

        System.out.println("Deleted note [" + shortId(existing.getId()) + "] \"" + existing.getTitle() + "\"");
    }

    private static Note resolveNote(Scanner scanner, NoteService service) {
        System.out.print("Enter note ID (or first 8 characters): ");
        String input = scanner.nextLine().trim();

        try {
            return service.getNoteById(UUID.fromString(input));
        } catch (IllegalArgumentException ignored) {
            List<Note> matches = service.getAllNotes().stream()
                    .filter(note -> note.getId().toString().startsWith(input))
                    .toList();

            if (matches.isEmpty()) {
                throw new IllegalArgumentException("No note found matching ID: " + input);
            }

            if (matches.size() > 1) {
                throw new IllegalArgumentException("Multiple notes match prefix \"" + input + "\". Enter more characters.");
            }

            return matches.getFirst();
        }
    }

    private static void printNotes(String header, List<Note> notes) {
        System.out.println("\n--- " + header + " (" + notes.size() + ") ---");
        if (notes.isEmpty()) {
            System.out.println("No notes found.");
            return;
        }

        for (Note note : notes) {
            String preview = note.getContent().length() > 45
                    ? note.getContent().substring(0, 42) + "..."
                    : note.getContent();

            System.out.printf("[%s] %-25s | Updated: %s | %s%n",
                    shortId(note.getId()),
                    note.getTitle(),
                    note.getUpdateTime().format(FORMATTER),
                    preview);
        }
    }

    private static String shortId(UUID id) {
        return id.toString().substring(0, 8);
    }
}