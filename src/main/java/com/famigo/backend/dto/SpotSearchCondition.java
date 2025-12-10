package com.famigo.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "スポット検索条件データモデル（検索フォームから送られる条件）")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class SpotSearchCondition {

  @Schema(description = "カテゴリID一覧（複数選択可）", example = "[1, 3]")
  private List<Long> categoryIds;

  @Schema(description = "住所・エリア・スポット名などのキーワード", example = "京都市")
  private String address;

  @Schema(description = "予算フィルタ：free, 1000, 2000, over2000", example = "[\"free\", \"1000\"]")
  private List<String> price;

  @Schema(description = "対象年齢フィルタ：toddler, eleLow, eleHigh, juniorHigh", example = "[\"toddler\", \"eleLow\"]")
  private List<String> age;

  @Schema(description = "設備フィルタ：diaper, stroller, playground, athletics, water, indoor", example = "[\"diaper\", \"indoor\"]")
  private List<String> facilities;

}