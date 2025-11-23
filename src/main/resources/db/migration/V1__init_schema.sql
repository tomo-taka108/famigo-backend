-- ============================================
-- V1 初期スキーマ（テーブル構成）
-- users / categories / spots / spot_facilities / reviews / favorites
-- ============================================

-- 1. users テーブル（ユーザー情報）
CREATE TABLE IF NOT EXISTS users (                               -- 一般ユーザー／管理者アカウントを管理するテーブル
    id BIGINT AUTO_INCREMENT PRIMARY KEY,                        -- ユーザーID（PK）
    name VARCHAR(100) NOT NULL,                                  -- 氏名 or ニックネーム
    email VARCHAR(255) NOT NULL UNIQUE,                          -- メールアドレス（ログインID）一意制約あり
    password_hash VARCHAR(255) NOT NULL,                         -- パスワードハッシュ
    role VARCHAR(20) NOT NULL,                                   -- 権限区分（USER / ADMIN）
    created_at DATETIME NOT NULL,                                -- 登録日時
    updated_at DATETIME NOT NULL,                                -- 更新日時
    is_deleted TINYINT(1) NOT NULL DEFAULT 0                     -- 論理削除フラグ（0:有効, 1:削除）
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;                         -- InnoDB＋UTF-8で作成

-- 2. categories テーブル（カテゴリマスタ）
CREATE TABLE IF NOT EXISTS categories (                          -- スポットのカテゴリ（公園 / 屋内施設 / 季節スポットなど）
    id INT AUTO_INCREMENT PRIMARY KEY,                           -- カテゴリID（PK）
    name VARCHAR(50) NOT NULL UNIQUE                             -- カテゴリ名（公園 / 屋内施設 / 季節スポット等）一意制約
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;                         -- InnoDB＋UTF-8で作成

-- 3. spots テーブル（お出かけスポット本体）
CREATE TABLE IF NOT EXISTS spots (                               -- 公園・屋内施設・スキー場などのスポット本体
    id BIGINT AUTO_INCREMENT PRIMARY KEY,                        -- スポットID（PK）
    category_id INT NOT NULL,                                    -- カテゴリID（FK → categories.id）
    name VARCHAR(200) NOT NULL,                                  -- スポット名
    address VARCHAR(255) NOT NULL,                               -- 住所（都道府県〜番地）
    area VARCHAR(100) NOT NULL,                                  -- 市区町村・エリア名（検索用）
    price_type VARCHAR(50) NOT NULL,                             -- 予算（無料 / 1000円以内 / 2000円以内 /　2000円以上）Enum
    parking_info VARCHAR(255) NULL,                              -- 駐車場情報（有無・箇所数・総台数など自由入力）
    toilet_info VARCHAR(255) NULL,                               -- トイレ情報（数・場所など自由入力）
    target_age VARCHAR(50) NULL,                                 -- 対象年齢帯（幼児 / 小学生など自由入力）
    staying_time VARCHAR(20) NULL,                               -- 滞在時間目安（1時間以内 / 2時間以内 / 半日 / 1日）Enum
    convenience_store VARCHAR(100) NULL,                         -- コンビニの有無・距離メモ
    restaurant_info VARCHAR(100) NULL,                           -- 飲食店情報（フードコート等）
    google_map_url VARCHAR(500) NULL,                            -- GoogleマップURL
    closed_days VARCHAR(100) NULL,                               -- 定休日（「火曜日」「不定休」など自由入力）
    official_url VARCHAR(255) NULL,                              -- 公式サイトURL
    notes TEXT NULL,                                             -- 備考・メモ
    created_at DATETIME NOT NULL,                                -- 登録日時
    updated_at DATETIME NOT NULL,                                -- 更新日時
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,                    -- 論理削除フラグ（0:有効, 1:削除）
    CONSTRAINT fk_spots_category                                 -- FK制約名（カテゴリへの外部キー）
        FOREIGN KEY (category_id) REFERENCES categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;                         -- InnoDB＋UTF-8で作成

-- spots 用のインデックス（検索・JOIN高速化）※検索によく使いそうなカラムのみに絞って設定しておく
CREATE INDEX idx_spots_category_id ON spots (category_id);       -- カテゴリID別の検索を高速化
CREATE INDEX idx_spots_area ON spots (area);                     -- エリア別の検索を高速化
CREATE INDEX idx_spots_price_type ON spots (price_type);         -- 予算別の検索を高速化

-- 4. spot_facilities テーブル（スポット設備情報：1:1）
CREATE TABLE IF NOT EXISTS spot_facilities (                     -- スポットごとの設備情報を管理するテーブル
    spot_id BIGINT PRIMARY KEY,                                  -- スポットID（PKかつFK）spots と 1:1
    diaper_changing BOOLEAN NOT NULL DEFAULT 0,                  -- オムツ替えスペース有無（0:なし, 1:あり）
    stroller_ok BOOLEAN NOT NULL DEFAULT 0,                      -- ベビーカー利用可否
    playground BOOLEAN NOT NULL DEFAULT 0,                       -- 遊具あり
    athletics BOOLEAN NOT NULL DEFAULT 0,                        -- アスレチックあり
    water_play BOOLEAN NOT NULL DEFAULT 0,                       -- 水遊び可能
    indoor BOOLEAN NOT NULL DEFAULT 0,                           -- 屋内施設フラグ
    CONSTRAINT fk_spot_facilities_spot                           -- FK制約名（spots への外部キー）
        FOREIGN KEY (spot_id) REFERENCES spots(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;                         -- InnoDB＋UTF-8で作成

-- 5. reviews テーブル（レビュー投稿）
CREATE TABLE IF NOT EXISTS reviews (                             -- ユーザーが投稿するレビュー情報
    id BIGINT AUTO_INCREMENT PRIMARY KEY,                        -- レビューID（PK）
    spot_id BIGINT NOT NULL,                                     -- 対象スポットID（FK → spots.id）
    user_id BIGINT NOT NULL,                                     -- 投稿ユーザーID（FK → users.id）
    child_age_group VARCHAR(50) NULL,                            -- 子どもの年齢帯（幼児 / 小学生 など）
    rating INT NOT NULL,                                         -- 総合評価（1〜5）
    rating_cost INT NULL,                                        -- コスパ評価（1〜5）
    crowd_level INT NULL,                                        -- 混雑度（1〜5）
    toilet_cleanliness INT NULL,                                 -- トイレ清潔度（1〜5）
    stroller_ease INT NULL,                                      -- ベビーカーの使いやすさ（1〜5）
    review_text TEXT NOT NULL,                                   -- レビュー本文
    cost_total INT NULL,                                         -- かかった合計金額（円）
    created_at DATETIME NOT NULL,                                -- 投稿日時
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,                    -- 論理削除フラグ（0:有効, 1:削除）
    CONSTRAINT fk_reviews_spot                                   -- FK制約名（スポットへの外部キー）
        FOREIGN KEY (spot_id) REFERENCES spots(id),
    CONSTRAINT fk_reviews_user                                   -- FK制約名（ユーザーへの外部キー）
        FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;                         -- InnoDB＋UTF-8で作成

-- reviews の検索用インデックス
CREATE INDEX idx_reviews_spot_id ON reviews (spot_id);           -- スポット別レビュー一覧を高速化
CREATE INDEX idx_reviews_user_id ON reviews (user_id);           -- ユーザー別レビュー一覧を高速化

-- 6. favorites テーブル（お気に入り）
CREATE TABLE IF NOT EXISTS favorites (                           -- ユーザーのお気に入りスポットを管理するテーブル
    user_id BIGINT NOT NULL,                                     -- ユーザーID（FK → users.id）
    spot_id BIGINT NOT NULL,                                     -- スポットID（FK → spots.id）
    created_at DATETIME NOT NULL,                                -- お気に入り登録日時
    PRIMARY KEY (user_id, spot_id),                              -- 複合PK（同じ組合せを一意にする）
    CONSTRAINT fk_favorites_user                                 -- FK制約名（usersへの外部キー）
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_favorites_spot                                 -- FK制約名（spotsへの外部キー）
        FOREIGN KEY (spot_id) REFERENCES spots(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;                         -- InnoDB＋UTF-8で作成

-- favorites の検索用インデックス
CREATE INDEX idx_favorites_user_id ON favorites (user_id);       -- ユーザーごとのお気に入り一覧を高速化
CREATE INDEX idx_favorites_spot_id ON favorites (spot_id);       -- スポットごとのお気に入り数集計などを高速化
