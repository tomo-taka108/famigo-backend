package com.famigo.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "スポット一覧カード１件分のデータモデル")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class SpotListItemDto {

  @Schema(description = "スポットID（spots.id）", example = "2")
  private Long id;

  @Schema(description = "スポット名（spots.name）", example = "滋賀県希望ヶ丘文化公園")
  private String name;

  @Schema(description = "住所（spots.address）", example = "滋賀県野洲市北桜978")
  private String address;

  @Schema(description = "エリア名（spots.area）", example = "野洲市")
  private String area;

  @Schema(description = "予算区分（spots.price_type）", example = "1000円以内")
  private String priceType;

  @Schema(description = "カテゴリ名（categories.name）", example = "公園")
  private String categoryName;

  @Schema(description = "対象年齢（spots.target_age）", example = "小学校低学年まで")
  private String targetAge;

  @Schema(description = "GoogleマップURL（spots.google_map_url）")
  private String googleMapUrl;

  @Schema(description = "お気に入り済みかどうか（favorites）", example = "true")
  private Boolean isFavorite;

  // ▼ spot_facilities テーブル由来の設備フラグ
  @Schema(description = "オムツ替えスペースの有無（diaper_changing）", example = "true")
  private Boolean diaperChanging;

  @Schema(description = "ベビーカー利用可否（stroller_ok）", example = "true")
  private Boolean strollerOk;

  @Schema(description = "遊具があるか（playground）", example = "true")
  private Boolean playground;

  @Schema(description = "アスレチックコースがあるか（athletics）", example = "false")
  private Boolean athletics;

  @Schema(description = "水遊びが可能か（water_play）", example = "true")
  private Boolean waterPlay;

  @Schema(description = "屋内施設か（indoor）", example = "false")
  private Boolean indoor;

}
