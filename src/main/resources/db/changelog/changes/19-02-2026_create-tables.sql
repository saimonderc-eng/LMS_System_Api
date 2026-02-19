-- liquibase formatted sql

-- changeset magamed:1
CREATE TABLE IF NOT EXISTS attachments
(
    ID           BIGSERIAL PRIMARY KEY,
    NAME         VARCHAR(255) NOT NULL UNIQUE,
    URL          VARCHAR(255) NOT NULL UNIQUE,
    LESSON_ID    BIGINT       NOT NULL,
    CREATED_TIME TIMESTAMP,

    FOREIGN KEY (LESSON_ID) REFERENCES public.lessons (id) ON DELETE CASCADE
);