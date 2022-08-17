ALTER TABLE t_user
    ADD registration_type INTEGER;

ALTER TABLE t_user
    ALTER COLUMN registration_type SET NOT NULL;