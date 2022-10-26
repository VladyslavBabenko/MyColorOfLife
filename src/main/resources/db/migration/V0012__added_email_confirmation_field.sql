ALTER TABLE t_user
    ADD is_email_confirmed BOOLEAN;

ALTER TABLE t_user
    ALTER COLUMN is_email_confirmed SET NOT NULL;