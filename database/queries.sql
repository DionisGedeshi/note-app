TRUNCATE TABLE notes;

INSERT INTO notes(
    id,
    title,
    content,
    created_at,
    updated_at,
    archived
)
VALUES (
           '550e8400-e29b-41d4-a716-446655440001',
           'SQL Test',
        'Architecture review'
           '2026-07-21 21:47:10',
           '2026-07-21 21:47:10',
           FALSE
       );

INSERT INTO notes(
    id,
    title,
    content,
    created_at,
    updated_at,
    archived
)
VALUES (
           '550e8400-e29b-41d4-a716-446655440002',
           'SQL',
           'Learning the language',
           '2026-07-21 21:49:10',
           '2026-07-21 21:49:10',
           TRUE
       );

INSERT INTO notes(
    id,
    title,
    content,
    created_at,
    updated_at,
    archived
)
VALUES (
           '550e8400-e29b-41d4-a716-446655440003',
           'Hello',
           'Hello World',
           '2026-07-21 21:51:10',
           '2026-07-21 21:51:10',
           FALSE
       );

INSERT INTO notes(
    id,
    title,
    content,
    created_at,
    updated_at,
    archived
)
VALUES (
           '550e8400-e29b-41d4-a716-446655440004',
           'Hello Java',
           'Learning Java and SQL',
           '2026-07-22 14:44:05',
           '2026-07-22 14:44:05',
           TRUE
       );

INSERT INTO notes(
    id,
    title,
    content,
    created_at,
    updated_at,
    archived
)
VALUES (
           '550e8400-e29b-41d4-a716-446655440005',
           'Test JAVA',
        'PostgreSQL Indexing'
           '2026-07-22 11:44:05',
           '2026-07-22 13:20:45',
           TRUE
       );

INSERT INTO notes(
    id,
    title,
    content,
    created_at,
    updated_at,
    archived
)
VALUES (
           '550e8400-e29b-41d4-a716-446655440006',
        'Test',
        'Test'
           '2026-07-20 14:32:55',
           '2026-07-22 14:11:59',
           FALSE
       );

INSERT INTO notes(
    id,
    title,
    content,
    created_at,
    updated_at,
    archived
)
VALUES (
           '550e8400-e29b-41d4-a716-446655440007',
           '@#!$%',
           'TESTING for Titles containing SPECIAL CHARACTERS!',
           '2026-03-25 11:04:09',
           '2026-07-22 02:44:55',
           FALSE
       );

INSERT INTO notes(
    id,
    title,
    content,
    created_at,
    updated_at,
    archived
)
VALUES (
           '550e8400-e29b-41d4-a716-446655440008',
           'JaVaaAA',
           '',
           '2026-02-26 20:59:59',
           '2026-07-22 12:59:59',
           TRUE
       );

INSERT INTO notes(
    id,
    title,
    content,
    created_at,
    updated_at,
    archived
)
VALUES (
           '550e8400-e29b-41d4-a716-446655440009',
        '',
        '',
           '2026-04-22 21:44:35',
           '2026-07-20 12:54:25',
           FALSE
       );

INSERT INTO notes(
    id,
    title,
    content,
    created_at,
    updated_at,
    archived
)
VALUES (
           '550e8400-e29b-41d4-a716-446655440010',
           '10th NOteEeEE',
           'Note containing text and special characters.!@#.',
           '2025-09-29 12:59:05',
           '2025-09-29 12:59:05',
           TRUE
       );

SELECT title, updated_at
FROM notes;

SELECT *
FROM notes
WHERE archived = true;

SELECT *
FROM notes
WHERE title ILIKE '%java%';

SELECT *
FROM notes
WHERE title ILIKE 'i%';

SELECT *
FROM notes
WHERE created_at BETWEEN '2026-06-23 00:00:00' AND '2026-07-23 10:22:00';

SELECT *
FROM notes
ORDER BY updated_at desc;

SELECT *
FROM notes
ORDER BY updated_at desc
LIMIT 3;

SELECT COUNT(*) AS total_notes
FROM notes;

SELECT COUNT(*) AS archived_notes
FROM notes
WHERE archived = true;

SELECT MIN(created_at) AS oldest_created_note
FROM notes;

SELECT MAX(updated_at) AS newest_updated_note
FROM notes;

SELECT COUNT(*) AS total_archived_notes
FROM notes
WHERE archived = true;

SELECT COUNT(*) AS total_non_archived_notes
FROM notes
WHERE archived = false;

SELECT DISTINCT *
FROM notes
WHERE archived = true;

SELECT DISTINCT *
FROM notes
WHERE archived = false;

SELECT *
FROM notes
WHERE content = '';

SELECT *
FROM notes
WHERE LENGTH(content) > 0;