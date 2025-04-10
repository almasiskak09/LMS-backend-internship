/*сначала удаляем дочерние,потом родителей*/
DELETE FROM lesson;
ALTER TABLE lesson ALTER COLUMN ID RESTART WITH 1;

DELETE FROM chapter;
ALTER TABLE chapter ALTER COLUMN ID RESTART WITH 1; /*после удаления таблицы, для следующего теста
                                                      id снова начинается с "1" */

DELETE FROM course;
ALTER TABLE course ALTER COLUMN ID RESTART WITH 1;

