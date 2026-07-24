package com.dioni.notes.model;

import java.util.UUID;
import java.time.LocalDateTime;

public class Note {
    private UUID id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean archived;

    public Note(
            String title,
            String content
    ) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank.");
        }

        if (content == null) {
            this.content = "";
        } else {
            this.content = content;
        }

        LocalDateTime now = LocalDateTime.now();

        this.id = UUID.randomUUID();
        this.title = title;
        this.content = content;
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
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.archived = archived;
    }

    public void printNoteInfo() {
        System.out.println(id + title + content + createdAt + updatedAt);
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
}