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
FROM t_secure_token;
DELETE
FROM t_user;

DELETE
FROM t_role
WHERE id > 3;

INSERT INTO t_role
VALUES (4, 'ROLE_COURSE_OWNER_TEST', '"Власник курсу Test"'),
       (5, 'ROLE_COURSE_OWNER_DIFFERENT_COURSE', '"Власник курсу Different Course"'),
       (6, 'ROLE_COURSE_OWNER_TEST_COURSE', '"Власник курсу Test Course"');

INSERT INTO t_user /* encryptedPassword: $2a$10$Aj0/QMUfy7Fz5u4GxDviueq3tmqCsOBoOvUyxn8Yn8ncn6KK97Mby | rawPassword: 123456 */
VALUES (1, 'TestUser', 'TestUser@mail.com', '$2a$10$Aj0/QMUfy7Fz5u4GxDviueq3tmqCsOBoOvUyxn8Yn8ncn6KK97Mby', 0, true, 0, false),
       (2, 'TestAdmin', 'TestAdmin@mail.com', '$2a$10$Aj0/QMUfy7Fz5u4GxDviueq3tmqCsOBoOvUyxn8Yn8ncn6KK97Mby', 0, true,
        0, false),
       (3, 'TestAuthor', 'TestAuthor@mail.com', '$2a$10$Aj0/QMUfy7Fz5u4GxDviueq3tmqCsOBoOvUyxn8Yn8ncn6KK97Mby', 0,
        true, 0, false),
       (4, 'TestUserGAuth', 'TestUserGAuth@gmail.com', null, 1, false, 0, true);

INSERT INTO t_user_roles
VALUES (1, 1),
       (1, 2),
       (2, 2),
       (1, 3),
       (3, 3),
       (1, 4);

/*CourseOwnerRole*/
INSERT INTO t_user_roles
VALUES (4, 1),
       (4, 2);

INSERT INTO t_article
VALUES (1, '2022.08.05 22:00', 'First test title', 'First test text. First test text. First test text.'),
       (2, '2022.08.05 22:05', 'Second test title', 'Second test text. Second test text. Second test text.');

INSERT INTO t_article_users
VALUES (1, 2),
       (2, 3);

INSERT INTO t_event
VALUES (1, '2022.08.05 22:00', 'First test title', 'First test text. First test text. First test text.'),
       (2, '2022.08.05 22:05', 'Second test title', 'Second test text. Second test text. Second test text.');

INSERT INTO t_event_users
VALUES (1, 2),
       (2, 3);

INSERT INTO t_course_title
VALUES (1, 'Test', 'Test description'),
       (2, 'Different Course', 'Different Course description'),
       (3, 'Test Course', 'Test Course description');

INSERT INTO t_course
VALUES (1, 1, null, null, 'Test Text 1', 1),
       (2, 2, 'Test Video Title 2', 'Test Video Link 2', 'Test Text 2', 1),
       (3, 3, 'Test Video Title 3', 'Test Video Link 3', 'Test Text 3', 1),
       (4, 4, null, null, 'Test Text 4', 1),
       (5, 5, 'Test Video Title 5', 'Test Video Link 5', 'Test Text 5', 1),

       (6, 1, null, null, 'Different Course Test Text 1', 2),

       (7, 1, null, null, 'Test Course Text 1', 3),
       (8, 2, 'Test Course Video Title 2', 'Test Course Video Link 2', 'Test Course Text 2', 3),
       (9, 3, 'Test Course Video Title 3', 'Test Course Video Link 3', 'Test Course Text 3', 3),
       (10, 4, null, null, 'Test Text 4', 3),
       (11, 5, 'Test Course Video Title 5', 'Test Course Video Link 5', 'Test Course Text 5', 3);

INSERT INTO t_course_progress
VALUES (1, 1, 1),
       (2, 2, 1),
       (3, 1, 2);

INSERT INTO t_activation_code
VALUES (1, 'Q5sxTc941iokNy8', 1, 1),
       (2, 'Yx5ui2nqx98m92x', 1, 2);

INSERT INTO t_secure_token
VALUES (1, 'wMQzFUNrjsXyyht0lF-B', now(), now() + '05:00:00', 1);