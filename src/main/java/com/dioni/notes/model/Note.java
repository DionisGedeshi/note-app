package com.dioni.notes.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Note {
    private final UUID id;
    private String title;
    private String content;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean archived;

    public Note(String title, String content) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank.");
        }

        LocalDateTime now = LocalDateTime.now();

        this.id = UUID.randomUUID();
        this.title = title;
        this.content = (content == null) ? "" : content;
        this.createdAt = now;
        this.updatedAt = now;
        this.archived = false;

    }

    public Note(
            UUID id,
            String title,
            String content,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            boolean archived
    ) {
        this.id = id;
        this.title = title;
        this.content = (content == null) ? "" : content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.archived = archived;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreationTime() {
        return createdAt;
    }

    public LocalDateTime getUpdateTime() {
        return updatedAt;
    }

    public boolean isArchived() {
        return archived;
    }

    public void updateNote(String newTitle, String newContent) {
        if (newTitle == null || newTitle.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank.");
        }

        this.title = newTitle;
        this.content = (newContent == null) ? "" : newContent;
        this.updatedAt = LocalDateTime.now();
    }

    public void archive() {
        this.archived = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void unarchive() {
        this.archived = false;
        this.updatedAt = LocalDateTime.now();
    }
}