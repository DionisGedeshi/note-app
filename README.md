# Notes Vault

![Java CI with Maven](https://github.com/DionisGedeshi/note-app/actions/workflows/maven.yml/badge.svg)

A layered Java CLI note-taking application supporting PostgreSQL JDBC persistence with an automatic in-memory fallback.

## Architecture

- **`model`** — Domain entity (`Note`) enforcing title invariants, timestamps, and archive state transitions.
- **`repository`** — Storage abstraction (`NoteRepository`) with an `InMemoryNoteRepository` implementation.
- **`database`** — Pure JDBC PostgreSQL persistence (`JdbcNoteRepository`, `DatabaseConnection`) using parameterized `PreparedStatement` queries and `ILIKE` search.
- **`service`** — Business logic (`NoteService`) decoupled from storage details.
- **`app`** — Interactive terminal CLI (`NotesApplication`) with short-UUID prefix lookup.

## Quick Start

### Run in In-Memory Mode (Zero Setup)
```bash
mvn compile exec:java