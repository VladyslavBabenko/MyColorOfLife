ALTER TABLE t_secure_token
    ADD purpose INTEGER;

ALTER TABLE t_secure_token
    ALTER COLUMN purpose SET NOT NULL;