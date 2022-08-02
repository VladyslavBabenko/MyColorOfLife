DELETE
FROM t_user_roles;
DELETE
FROM t_user;

INSERT INTO t_user /* encryptedPassword: $2a$10$Aj0/QMUfy7Fz5u4GxDviueq3tmqCsOBoOvUyxn8Yn8ncn6KK97Mby | rawPassword: 123456 */
VALUES (1, 'TestUser', 'TestUser@mail.com', '$2a$10$Aj0/QMUfy7Fz5u4GxDviueq3tmqCsOBoOvUyxn8Yn8ncn6KK97Mby'),
       (2, 'TestAdmin', 'TestAdmin@mail.com', '$2a$10$Aj0/QMUfy7Fz5u4GxDviueq3tmqCsOBoOvUyxn8Yn8ncn6KK97Mby');

INSERT INTO t_user_roles
VALUES (1, 1),
       (1, 2),
       (2, 2);