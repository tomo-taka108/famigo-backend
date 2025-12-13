package com.famigo.backend.dto;

import com.famigo.backend.enums.AgeGroup;
import com.famigo.backend.enums.PriceType;
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

  @Schema(description = "キーワード（スポット名/住所/エリアをまとめて部分一致検索）", example = "京都市")
  private String keyword;

  @Schema(description = "予算フィルタ（複数選択可：Enum名を指定）", example = "[\"FREE\", \"UNDER_1000\"]")
  private List<PriceType> price;

  @Schema(description = "対象年齢帯フィルタ（複数選択可：Enum名を指定）", example = "[\"PRESCHOOL\", \"ELE_LOW\"]")
  private List<AgeGroup> age;

  @Schema(description = "設備フィルタ（複数選択可：diaper, stroller, playground, athletics, water, indoor）", example = "[\"diaper\", \"indoor\"]")
  private List<String> facilities;

}