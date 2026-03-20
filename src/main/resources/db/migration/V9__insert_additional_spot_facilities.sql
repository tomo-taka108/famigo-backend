-- ============================================
-- V9 追加スポット設備データ投入
-- 公開済み環境向けに spot_facilities テーブルへ 2件追加
-- ============================================

INSERT INTO spot_facilities (
    spot_id,
    diaper_changing,
    stroller_ok,
    playground,
    athletics,
    water_play,
    indoor,
    created_at,
    updated_at,
    is_deleted
) VALUES
  -- 11: 大泉緑地
  (11,
   1,
   1,
   1,
   1,
   0,
   0,
   NOW(),
   NOW(),
   0
  ),

  -- 12: ニフレル
  (12,
   1,
   1,
   0,
   0,
   0,
   1,
   NOW(),
   NOW(),
   0
  );