package com.dioni.notes.model;

import java.util.UUID;
import java.time.LocalDateTime;

public class Note {
    private UUID id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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
}