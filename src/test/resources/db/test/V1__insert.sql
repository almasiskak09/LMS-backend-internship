INSERT INTO course ( course_name, course_description, created_time, updated_time)
VALUES
    ( 'Java Developer', 'Learn Java programming', '2024-04-07 12:00', '2024-04-07 12:00'),
    ( 'Python Developer', 'Learn Python programming', '2024-04-07 12:00', '2024-04-07 12:00'),
    ( 'Web Developer', 'Learn front-end and back-end development', '2024-04-07 12:00', '2024-04-07 12:00'),
    ( 'Mobile Developer', 'Learn mobile app development', '2024-04-07 12:00', '2024-04-07 12:00'),
    ( 'Data Scientist', 'Learn data science techniques', '2024-04-07 12:00', '2024-04-07 12:00');

INSERT INTO chapter (chapter_name, chapter_description,chapter_order, course_id, created_time, updated_time)
VALUES
    ('Основы, синтаксис языка', 'Java - это простой, объектно-ориентированный язык', 1,1, '2024-04-07 12:00', '2024-04-07 12:00'),
    ('Условия, условные операторы', 'Условия в Java подобны магии, которая позволяет нам создавать виртуальные миры.', 2,1, '2024-04-07 12:00', '2024-04-07 12:00');

INSERT INTO lesson (lesson_name,lesson_description,lesson_content,chapter_id,lesson_order)
VALUES
    ('Переменные','Все про переменные','Very Long Text',1,1),
    ('Try-catch','Блоки Try-catch','Medium Long Text',2,2)


