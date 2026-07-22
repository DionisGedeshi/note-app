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
        'Agreesive ahh language',
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
        'Hi mom',
        'Hello World',
        '2026-07-21 21:51:10',
        '2026-07-21 21:51:10',
        FALSE
       );

SELECT *
FROM notes;

SELECT *
FROM notes
WHERE archived = FALSE;

UPDATE notes
SET title = 'Updating note manually',
    updated_at = '2026-07-21 21:53:10'
WHERE id = '550e8400-e29b-41d4-a716-446655440001';

DELETE FROM notes
WHERE id = '550e8400-e29b-41d4-a716-446655440002';

SELECT *
FROM notes;