package com.famigo.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "レビュー編集・削除の認可判定に必要な最小情報")
@Getter
@AllArgsConstructor
public class ReviewAuthInfoDto {

  @Schema(description = "レビューID", example = "10")
  private Long id;

  @Schema(description = "スポットID", example = "1")
  private Long spotId;

  @Schema(description = "投稿ユーザーID", example = "1")
  private Long userId;

  @Schema(description = "論理削除フラグ（0:有効, 1:削除）", example = "0")
  private Integer isDeleted;

}
