package com.famigo.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "スポット（spotsテーブル）のエンティティ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Spot {

  @Schema(description = "スポットID", example = "2")
  private Long id;

  @Schema(description = "カテゴリID（categories.id）", example = "1")
  private Integer categoryId;

  @Schema(description = "スポット名", example = "滋賀県希望ヶ丘文化公園")
  private String name;

  @Schema(description = "住所", example = "滋賀県野洲市北桜978")
  private String address;

  @Schema(description = "エリア名", example = "野洲市")
  private String area;

  @Schema(description = "価格区分", example = "1000円以内")
  private String priceType;

  @Schema(description = "駐車場情報", example = "あり、1箇所、合計約500台")
  private String parkingInfo;

  @Schema(description = "トイレ情報", example = "あり、約5箇所")
  private String toiletInfo;

  @Schema(description = "対象年齢", example = "年齢問わない")
  private String targetAge;

  @Schema(description = "滞在目安時間", example = "1日")
  private String stayingTime;

  @Schema(description = "近くのコンビニ情報", example = "付近になし")
  private String convenienceStore;

  @Schema(description = "飲食店情報", example = "レストランあり")
  private String restaurantInfo;

  @Schema(description = "GoogleマップURL")
  private String googleMapUrl;

  @Schema(description = "休園日", example = "10月～2月の月曜日、12月29日～1月3日")
  private String closedDays;

  @Schema(description = "公式サイトURL")
  private String officialUrl;

  @Schema(description = "備考（メモ）", example = "かなり広大、アスレチックコースは本格的")
  private String notes;

  @Schema(description = "作成日時（DATETIME）", example = "2025-01-01T12:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "更新日時（DATETIME）", example = "2025-01-01T12:00:00")
  private LocalDateTime updatedAt;

  @Schema(description = "論理削除フラグ", example = "false")
  private Boolean isDeleted;
}
