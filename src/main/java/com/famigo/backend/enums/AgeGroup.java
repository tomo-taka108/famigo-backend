package com.famigo.backend.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "対象年齢（スポット検索条件や表示に利用する）")
@Getter

public enum AgeGroup {

  ALL("全年齢"),
  PRESCHOOL("未就学児まで"),
  ELE_LOW("小学校低学年まで"),
  ELE_HIGH("小学校高学年まで"),
  JUNIOR_HIGH("中学生まで");

  @Schema(description = "画面表示・DB保存に使う値（日本語ラベル）", example = "未就学児まで")
  private final String value;

  AgeGroup(String value) {
    this.value = value;
  }
}
