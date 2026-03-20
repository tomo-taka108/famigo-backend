-- ============================================
-- V8 追加スポット初期データ投入
-- 公開済み環境向けに spots テーブルへ 2件追加
-- ============================================

INSERT INTO spots (
    id,
    category_id,
    name,
    address,
    area,
    price_type,
    parking_info,
    toilet_info,
    target_age,
    staying_time,
    convenience_store,
    restaurant_info,
    google_map_url,
    closed_days,
    official_url,
    notes,
    created_at,
    updated_at,
    is_deleted
) VALUES
  -- 11: 大泉緑地（公園）
  (11,
   1,
   '大泉緑地',
   '大阪府堺市北区金岡町128',
   '堺市',
   '1000円以内',
   'あり、3箇所、合計約1000台',
   'あり、約5箇所以上',
   '全年齢',
   '1日',
   '徒歩10分以内',
   'レストランあり',
   'https://www.google.co.jp/maps/place/%E5%A4%A7%E6%B3%89%E7%B7%91%E5%9C%B0/@34.5668543,135.5272576,1566m/data=!3m1!1e3!4m6!3m5!1s0x6000d9680f3cf4d3:0x80e1a949962e6911!8m2!3d34.5648719!4d135.5275278!16s%2Fg%2F120x76g5?entry=ttu&g_ep=EgoyMDI2MDMxNy4wIKXMDSoASAFQAw%3D%3D',
   '年中無休（施設により異なる）',
   'https://www.osaka-park.or.jp/nanbu/oizumi/main.html',
   '1日たっぷり遊べる広大な緑の楽園、多数の遊具あり',
   NOW(),
   NOW(),
   0
  ),

  -- 12: ニフレル（水族館）
  (12,
   3,
   'NIFREL（ニフレル）',
   '大阪府吹田市千里万博公園2-1 EXPOCITY内',
   '吹田市',
   '2000円以内',
   'あり（EXPOCITY内）',
   'あり、2箇所',
   '全年齢',
   '2時間以内',
   '徒歩5分以内',
   'レストラン多数あり',
   'https://www.google.co.jp/maps/place/%E3%83%8B%E3%83%95%E3%83%AC%E3%83%AB/@34.8063187,135.5310908,929m/data=!3m2!1e3!5s0x6000fcad4628734b:0x6d7605b3c383d3f1!4m14!1m7!3m6!1s0x6000fcad50479643:0xd55fc65092ad11c7!2z44OL44OV44Os44Or!8m2!3d34.8063187!4d135.5336657!16s%2Fg%2F11b7vk668h!3m5!1s0x6000fcad50479643:0xd55fc65092ad11c7!8m2!3d34.8063187!4d135.5336657!16s%2Fg%2F11b7vk668h?entry=ttu&g_ep=EgoyMDI2MDMxNy4wIKXMDSoASAFQAw%3D%3D',
   '年中無休',
   'https://www.nifrel.jp/',
   '水族館と動物園が融合した感性体験型施設',
   NOW(),
   NOW(),
   0
  );