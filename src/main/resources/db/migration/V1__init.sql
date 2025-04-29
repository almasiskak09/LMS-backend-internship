CREATE TABLE course
(
    id BIGSERIAL PRIMARY KEY ,
    course_name VARCHAR(255) NOT NULL UNIQUE ,
    course_description TEXT ,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE chapter
(
    id BIGSERIAL PRIMARY KEY ,
    chapter_name VARCHAR(255) NOT NULL UNIQUE ,
    chapter_description TEXT,
    chapter_order BIGSERIAL,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    course_id BIGINT NOT NULL,
    CONSTRAINT fk_chapter_to_course FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
);
CREATE INDEX idx_chapter_course ON chapter (course_id);

CREATE TABLE lesson
(
    id BIGSERIAL PRIMARY KEY,
    lesson_name VARCHAR(255) NOT NULL UNIQUE ,
    lesson_description TEXT,
    lesson_content TEXT,
    lesson_order BIGSERIAL,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    chapter_id BIGINT NOT NULL,
    CONSTRAINT fk_lesson_to_chapter FOREIGN KEY (chapter_id) REFERENCES chapter(id) ON DELETE CASCADE
);
CREATE INDEX idx_lesson_chapter ON lesson (chapter_id);

CREATE TABLE attachments
(
    id BIGSERIAL PRIMARY KEY,
    attachment_name VARCHAR(255),
    url TEXT,
    lesson_id BIGINT NOT NULL,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_attachment_to_lesson FOREIGN KEY (lesson_id) REFERENCES lesson(id) ON DELETE CASCADE
)