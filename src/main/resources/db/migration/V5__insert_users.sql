-- ============================================
-- V5 users 初期データ投入（開発・動作確認用）
-- ============================================

INSERT INTO users (
    id,
    name,
    email,
    password_hash,
    role,
    created_at,
    updated_at,
    is_deleted
) VALUES
  (
   1,
   'テスト太郎',
   'test1@example.com',
   '$2a$10$dummy_hash_for_dev_1',
   'USER',
   NOW(),
   NOW(),
   0
  ),

  (
   2,
   'テスト花子',
   'test2@example.com',
   '$2a$10$dummy_hash_for_dev_2',
   'USER',
   NOW(),
   NOW(),
   0
  ),

  (
   3,
   '管理者サンプル',
   'admin@example.com',
   '$2a$10$dummy_hash_for_dev_admin',
   'ADMIN',
   NOW(),
   NOW(),
   0
  ),

  (
   4,
   '退会ユーザー（表示名テスト）',
   'deleted@example.com',
   '$2a$10$dummy_hash_for_dev_deleted',
   'USER',
   NOW(),
   NOW(),
   1
  );
