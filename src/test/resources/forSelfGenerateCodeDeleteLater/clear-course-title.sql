DELETE
FROM t_course_progress
WHERE course_id IN (SELECT id FROM t_course WHERE course_title_id = 3);
DELETE
FROM t_course
WHERE course_title_id = 3;
DELETE
FROM t_course_title
WHERE id = 3;