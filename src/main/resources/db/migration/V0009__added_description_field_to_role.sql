ALTER TABLE t_role
    ADD description VARCHAR(255);

UPDATE t_role SET description = 'Користувач' WHERE role_name = 'ROLE_USER';
UPDATE t_role SET description = 'Адміністратор' WHERE role_name = 'ROLE_ADMIN';
UPDATE t_role SET description = 'Автор' WHERE role_name = 'ROLE_AUTHOR';

ALTER TABLE t_role
    ALTER COLUMN description SET NOT NULL;