ALTER TABLE classes
    ALTER COLUMN tutor_id DROP NOT NULL;

ALTER TABLE classes
    ADD COLUMN IF NOT EXISTS note TEXT,
    ADD COLUMN IF NOT EXISTS display_name VARCHAR(255);

ALTER TABLE classes
    DROP CONSTRAINT IF EXISTS classes_status_check;

CREATE TABLE IF NOT EXISTS tutor_class_applications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    class_id UUID NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    tutor_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL,
    applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP,
    reviewed_by UUID REFERENCES users(id) ON DELETE SET NULL,
    rejection_reason TEXT
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_tutor_class_application_class_tutor
    ON tutor_class_applications(class_id, tutor_id);

CREATE INDEX IF NOT EXISTS idx_tutor_class_applications_status
    ON tutor_class_applications(status);

CREATE INDEX IF NOT EXISTS idx_tutor_class_applications_class_id
    ON tutor_class_applications(class_id);

INSERT INTO subjects (id, name, default_price_per_hour)
SELECT gen_random_uuid(), 'SAT Math', 180000.00
WHERE NOT EXISTS (SELECT 1 FROM subjects WHERE lower(name) = lower('SAT Math'));

INSERT INTO subjects (id, name, default_price_per_hour)
SELECT gen_random_uuid(), 'SAT Verbal', 230000.00
WHERE NOT EXISTS (SELECT 1 FROM subjects WHERE lower(name) = lower('SAT Verbal'));
