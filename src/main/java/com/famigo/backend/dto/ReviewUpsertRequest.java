package com.famigo.backend.dto;

import com.famigo.backend.enums.ChildAgeGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "レビュー投稿（Create）用リクエストデータモデル")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ReviewUpsertRequest {

  @Schema(description = "子どもの年齢帯（単一選択）", example = "PRESCHOOL")
  @NotNull
  private ChildAgeGroup childAgeGroup;

  @Schema(description = "総合評価（1〜5）", example = "5")
  @NotNull
  @Min(1)
  @Max(5)
  private Integer rating;

  @Schema(description = "コスパ評価（1〜5）", example = "4")
  @Min(1)
  @Max(5)
  private Integer ratingCost;

  @Schema(description = "混雑度（1〜5）", example = "3")
  @Min(1)
  @Max(5)
  private Integer crowdLevel;

  @Schema(description = "トイレ清潔度（1〜5）", example = "4")
  @Min(1)
  @Max(5)
  private Integer toiletCleanliness;

  @Schema(description = "ベビーカーの使いやすさ（1〜5）", example = "5")
  @Min(1)
  @Max(5)
  private Integer strollerEase;

  @Schema(description = "レビュー本文", example = "子どもが1日中遊べて大満足でした")
  @NotBlank
  private String reviewText;

  @Schema(description = "かかった合計金額（円）", example = "1000")
  @Min(0)
  private Integer costTotal;

}
