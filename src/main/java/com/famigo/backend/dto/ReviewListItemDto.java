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

  @Schema(description = "総合評価の点数（1〜5）", example = "4")
  private Integer rating;

  @Schema(description = "レビュー本文", example = "敷地が広くて遊具の種類も多く、子どもが一日中遊べました")
  private String reviewText;

  @Schema(description = "投稿日（reviews.created_at）", example = "2025-12-01T10:30:00")
  private LocalDateTime createdAt;

}
