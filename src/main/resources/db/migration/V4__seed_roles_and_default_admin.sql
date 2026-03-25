-- Seed system roles (idempotent)
INSERT INTO roles (id, name)
SELECT '11111111-1111-1111-1111-111111111111'::uuid, 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN');

INSERT INTO roles (id, name)
SELECT '22222222-2222-2222-2222-222222222222'::uuid, 'TUTOR'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'TUTOR');

INSERT INTO roles (id, name)
SELECT '33333333-3333-3333-3333-333333333333'::uuid, 'STUDENT'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'STUDENT');

-- Seed default admin user (idempotent)
INSERT INTO users (id, name, email, password, status, default_salary_rate, created_at, updated_at)
SELECT
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'::uuid,
    'System Admin',
    'trandinhtien05@gmail.com',
    '${default_admin_password_hash}',
    'ACTIVE',
    0.7500,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'trandinhtien05@gmail.com'
);

-- Keep seeded account active and ensure password exists.
UPDATE users
SET
    name = 'System Admin',
    status = 'ACTIVE',
    password = CASE
        WHEN password IS NULL OR BTRIM(password) = '' THEN '${default_admin_password_hash}'
        ELSE password
    END,
    updated_at = NOW()
WHERE email = 'trandinhtien05@gmail.com';

-- Assign ADMIN/TUTOR/STUDENT roles to the seeded account (idempotent)
INSERT INTO user_roles (id, user_id, role_id, status, revoked_reason, updated_by, created_at, updated_at)
SELECT
    ('00000000-0000-0000-0000-' || SUBSTRING(MD5(u.id::text || '-' || r.name), 1, 12))::uuid,
    u.id,
    r.id,
    'ACTIVE',
    NULL,
    u.id,
    NOW(),
    NOW()
FROM users u
JOIN roles r ON r.name IN ('ADMIN', 'TUTOR', 'STUDENT')
WHERE u.email = 'trandinhtien05@gmail.com'
  AND NOT EXISTS (
      SELECT 1
      FROM user_roles ur
      WHERE ur.user_id = u.id
        AND ur.role_id = r.id
  );
