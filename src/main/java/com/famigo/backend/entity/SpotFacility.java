package com.famigo.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "スポットの詳細な施設情報（spot_facilitiesテーブル）のエンティティ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class SpotFacility {

  @Schema(description = "スポットID（spots.id）", example = "2")
  private Long spotId;

  @Schema(description = "オムツ替えスペースの有無（diaper_changing）", example = "true")
  private Boolean diaperChanging;

  @Schema(description = "ベビーカー利用可否（stroller_ok）", example = "true")
  private Boolean strollerOk;

  @Schema(description = "遊具の有無（playground）", example = "true")
  private Boolean playground;

  @Schema(description = "アスレチックの有無（athletics）", example = "true")
  private Boolean athletics;

  @Schema(description = "水遊び可能か（water_play）", example = "true")
  private Boolean waterPlay;

  @Schema(description = "屋内施設か（indoor）", example = "false")
  private Boolean indoor;

  @Schema(description = "作成日時（DATETIME）", example = "2025-01-01T12:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "更新日時（DATETIME）", example = "2025-01-01T12:00:00")
  private LocalDateTime updatedAt;

  @Schema(description = "論理削除フラグ", example = "false")
  private Boolean isDeleted;

}
