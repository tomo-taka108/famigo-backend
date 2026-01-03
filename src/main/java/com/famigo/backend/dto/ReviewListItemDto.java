package com.famigo.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "レビュー一覧表示用のデータモデル")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ReviewListItemDto {

  @Schema(description = "レビューID（reviews.id）", example = "10")
  private Long id;

  @Schema(description = "スポットID（reviews.spot_id）", example = "3")
  private Long spotId;

  @Schema(description = "ユーザーID（reviews.user_id）", example = "5")
  private Long userId;

  @Schema(description = "投稿者名（users.name）", example = "たろうパパ")
  private String userName;

  @Schema(description = "子どもの年齢帯（単一選択）", example = "PRESCHOOL")
  private String childAgeGroup;

  @Schema(description = "総合評価（1〜5）", example = "4")
  private Integer rating;

  @Schema(description = "コスパ評価（1〜5）", example = "4")
  private Integer ratingCost;

  @Schema(description = "混雑度（1〜5）", example = "2")
  private Integer crowdLevel;

  @Schema(description = "トイレ清潔度（1〜5）", example = "5")
  private Integer toiletCleanliness;

  @Schema(description = "ベビーカーの使いやすさ（1〜5）", example = "4")
  private Integer strollerEase;

  @Schema(description = "レビュー本文", example = "敷地が広くて遊具の種類も多く、子どもが一日中遊べました")
  private String reviewText;

  @Schema(description = "かかった合計金額（円）", example = "1200")
  private Integer costTotal;

  @Schema(description = "投稿日時", example = "2025-12-01T10:30:00")
  private LocalDateTime createdAt;

  @Schema(description = "更新日時", example = "2025-12-01T12:00:00")
  private LocalDateTime updatedAt;

}
