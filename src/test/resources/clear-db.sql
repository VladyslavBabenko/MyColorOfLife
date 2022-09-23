DELETE
FROM t_article_users;
DELETE
FROM t_user_roles;
DELETE
FROM t_event_users;
DELETE
FROM t_course_progress;
DELETE
FROM t_activation_code;
DELETE
FROM t_course;
DELETE
FROM t_course_title;
DELETE
FROM t_event;
DELETE
FROM t_article;
DELETE
FROM t_user;

DELETE
FROM t_role
WHERE id > 3;