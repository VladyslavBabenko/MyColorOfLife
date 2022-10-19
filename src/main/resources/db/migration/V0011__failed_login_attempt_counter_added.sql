ALTER TABLE t_user
    ADD failed_login_attempt INTEGER;

ALTER TABLE t_user
    ALTER COLUMN failed_login_attempt SET NOT NULL;