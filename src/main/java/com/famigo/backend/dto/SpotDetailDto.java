package com.famigo.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "スポット詳細画面用のデータモデル")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class SpotDetailDto {

  @Schema(description = "スポットID（spots.id）", example = "2")
  private Long id;                                     // スポットID

  @Schema(description = "スポット名（spots.name）", example = "滋賀県希望ヶ丘文化公園")
  private String name;                                 // スポット名

  @Schema(description = "住所（spots.address）", example = "滋賀県野洲市北桜978")
  private String address;                              // 住所

  @Schema(description = "エリア名（spots.area）", example = "野洲市")
  private String area;                                 // エリア名

  @Schema(description = "予算区分（spots.price_type）", example = "1000円以内")
  private String priceType;                            // 予算区分

  @Schema(description = "カテゴリ名（categories.name）", example = "公園")
  private String categoryName;                         // カテゴリ名

  @Schema(description = "駐車場情報（spots.parking_info）")
  private String parkingInfo;                          // 駐車場情報メモ

  @Schema(description = "トイレ情報（spots.toilet_info）")
  private String toiletInfo;                           // トイレ情報メモ

  @Schema(description = "対象年齢（spots.target_age）", example = "小学校低学年まで")
  private String targetAge;                            // 対象年齢メモ

  @Schema(description = "滞在時間目安（spots.staying_time）", example = "半日")
  private String stayingTime;                          // 滞在時間目安

  @Schema(description = "コンビニ情報（spots.convenience_store）")
  private String convenienceStore;                     // コンビニ情報

  @Schema(description = "飲食店情報（spots.restaurant_info）")
  private String restaurantInfo;                       // 飲食店情報

  @Schema(description = "GoogleマップURL（spots.google_map_url）")
  private String googleMapUrl;                         // GoogleマップURL

  @Schema(description = "定休日（spots.closed_days）")
  private String closedDays;                           // 定休日

  @Schema(description = "公式サイトURL（spots.official_url）")
  private String officialUrl;                          // 公式サイトURL

  @Schema(description = "備考メモ（spots.notes）")
  private String notes;                                // 備考・メモ

  // ▼ spot_facilities テーブル由来の設備フラグ
  @Schema(description = "オムツ替えスペースの有無（diaper_changing）", example = "true")
  private Boolean diaperChanging;                      // オムツ替え

  @Schema(description = "ベビーカー利用可否（stroller_ok）", example = "true")
  private Boolean strollerOk;                          // ベビーカーOK

  @Schema(description = "遊具があるか（playground）", example = "true")
  private Boolean playground;                          // 遊具あり

  @Schema(description = "アスレチックコースがあるか（athletics）", example = "false")
  private Boolean athletics;                           // アスレチックコース

  @Schema(description = "水遊びが可能か（water_play）", example = "true")
  private Boolean waterPlay;                           // 水遊び

  @Schema(description = "屋内施設か（indoor）", example = "false")
  private Boolean indoor;                              // 屋内フラグ
}
