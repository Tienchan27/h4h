-- Some environments may still have a legacy check constraint that only allows
-- purpose='REGISTER'. Google-link OTP then fails at insert time and surfaces as 500.
-- Normalize constraints to include both REGISTER and GOOGLE_LINK.

DO $$
DECLARE con RECORD;
BEGIN
    FOR con IN
        SELECT c.conname
        FROM pg_constraint c
        WHERE c.conrelid = 'otp_verifications'::regclass
          AND c.contype = 'c'
          AND pg_get_constraintdef(c.oid) ILIKE '%purpose%'
    LOOP
        EXECUTE format('ALTER TABLE otp_verifications DROP CONSTRAINT IF EXISTS %I', con.conname);
    END LOOP;
END $$;

DO $$
DECLARE con RECORD;
BEGIN
    FOR con IN
        SELECT c.conname
        FROM pg_constraint c
        WHERE c.conrelid = 'otp_verifications'::regclass
          AND c.contype = 'c'
          AND pg_get_constraintdef(c.oid) ILIKE '%status%'
    LOOP
        EXECUTE format('ALTER TABLE otp_verifications DROP CONSTRAINT IF EXISTS %I', con.conname);
    END LOOP;
END $$;

ALTER TABLE otp_verifications
    ADD CONSTRAINT ck_otp_verifications_purpose
        CHECK (purpose IN ('REGISTER', 'GOOGLE_LINK'));

ALTER TABLE otp_verifications
    ADD CONSTRAINT ck_otp_verifications_status
        CHECK (status IN ('ACTIVE', 'VERIFIED', 'EXPIRED'));
