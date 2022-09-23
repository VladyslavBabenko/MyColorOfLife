ALTER TABLE t_course_title
    ADD description VARCHAR(200);

ALTER TABLE t_course_title
    ALTER COLUMN description SET NOT NULL;