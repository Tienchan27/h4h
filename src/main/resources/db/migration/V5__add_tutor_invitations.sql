CREATE TABLE IF NOT EXISTS tutor_invitations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    invited_by UUID NOT NULL REFERENCES users(id),
    invited_user_id UUID REFERENCES users(id),
    accepted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_tutor_invitations_status ON tutor_invitations(status);
CREATE INDEX IF NOT EXISTS idx_tutor_invitations_invited_by ON tutor_invitations(invited_by);
