DELETE
FROM t_article_users;
DELETE
FROM t_user_roles;
DELETE
FROM t_event_users;
DELETE
FROM t_event;
DELETE
FROM t_article;
DELETE
FROM t_user;

INSERT INTO t_user /* encryptedPassword: $2a$10$Aj0/QMUfy7Fz5u4GxDviueq3tmqCsOBoOvUyxn8Yn8ncn6KK97Mby | rawPassword: 123456 */
VALUES (1, 'TestUser', 'TestUser@mail.com', '$2a$10$Aj0/QMUfy7Fz5u4GxDviueq3tmqCsOBoOvUyxn8Yn8ncn6KK97Mby'),
       (2, 'TestAdmin', 'TestAdmin@mail.com', '$2a$10$Aj0/QMUfy7Fz5u4GxDviueq3tmqCsOBoOvUyxn8Yn8ncn6KK97Mby'),
       (3, 'TestAuthor', 'TestAuthor@mail.com', '$2a$10$Aj0/QMUfy7Fz5u4GxDviueq3tmqCsOBoOvUyxn8Yn8ncn6KK97Mby');

INSERT INTO t_user_roles
VALUES (1, 1),
       (1, 2),
       (2, 2),
       (1, 3),
       (3, 3);

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

