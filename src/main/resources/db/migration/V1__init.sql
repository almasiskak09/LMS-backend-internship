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
    chapter_order BIGINT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    course_id BIGINT NOT NULL,
    CONSTRAINT fk_chapter_to_course FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
);


CREATE TABLE lesson
(
    id BIGSERIAL PRIMARY KEY,
    lesson_name VARCHAR(255) NOT NULL UNIQUE ,
    lesson_description TEXT,
    lesson_content TEXT,
    lesson_order BIGINT ,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    chapter_id BIGINT NOT NULL,
    CONSTRAINT fk_lesson_to_chapter FOREIGN KEY (chapter_id) REFERENCES chapter(id) ON DELETE CASCADE
);