-- ============================================
-- V7 users デモアカウント整備（JWTログイン動作確認用）
--   ・過去の migration（V1～V6）は修正しない
--   ・V5のダミー password_hash を「実ログイン可能なBCrypt」に更新する
--   ・demo_user / demo_admin を追加投入する
-- ============================================

-- =========================================================
-- 1) V5で投入済みユーザーのダミーhashを本物BCryptに更新
--    対象（V5で確認済み）：
--      test1@example.com（id=1, USER）
--      test2@example.com（id=2, USER）
--      admin@example.com（id=3, ADMIN）
--
--    ログイン確認用の平文パスワード（固定）
--      test1 : test1234
--      test2 : test2345
--      admin : admin1234
-- =========================================================

-- test1@example.com / password: test1234
UPDATE users
SET
  password_hash = '$2b$10$vZ4FtVXIAM2bOOxh5MAVt.wz5GgOgp5TMLcRqrX9.1DyBgQD0758q',
  updated_at = NOW()
WHERE email = 'test1@example.com'
  AND is_deleted = 0;

-- test2@example.com / password: test2345
UPDATE users
SET
  password_hash = '$2b$10$MPZF6akpjWjy3mDPmkuAx.wnV4AbQDV3rfjPB7bXwQq5eyzJ2t0/u',
  updated_at = NOW()
WHERE email = 'test2@example.com'
  AND is_deleted = 0;

-- admin@example.com / password: admin1234
UPDATE users
SET
  password_hash = '$2b$10$hlulGs6K5CUhBGDacFQFQ.KQ3ifpns7.4zA1d11v/EfOzJbcpYxsG',
  updated_at = NOW()
WHERE email = 'admin@example.com'
  AND is_deleted = 0;


-- =========================================================
-- 2) demo_user（USER）を追加
--    email: demo_user@example.com
--    password: demo1234
-- =========================================================
INSERT INTO users (
  name,
  email,
  password_hash,
  role,
  created_at,
  updated_at,
  is_deleted
)
SELECT
  'デモユーザー',
  'demo_user@example.com',
  '$2b$10$5fpbfjgV//Z77Jw2rsziTOsufY5/Dsk5De5If28HH./SFlvN8.GPy',
  'USER',
  NOW(),
  NOW(),
  0
WHERE NOT EXISTS (
  SELECT 1 FROM users WHERE email = 'demo_user@example.com'
);


-- =========================================================
-- 3) demo_admin（ADMIN）を追加
--    email: demo_admin@example.com
--    password: admin1234
-- =========================================================
INSERT INTO users (
  name,
  email,
  password_hash,
  role,
  created_at,
  updated_at,
  is_deleted
)
SELECT
  'デモ管理者',
  'demo_admin@example.com',
  '$2b$10$hlulGs6K5CUhBGDacFQFQ.KQ3ifpns7.4zA1d11v/EfOzJbcpYxsG',
  'ADMIN',
  NOW(),
  NOW(),
  0
WHERE NOT EXISTS (
  SELECT 1 FROM users WHERE email = 'demo_admin@example.com'
);
