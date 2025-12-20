package com.famigo.backend.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "レビュー投稿時に指定する子どもの年齢帯（単一選択）")
@Getter
public enum ChildAgeGroup {

  PRESCHOOL("未就学児"),
  ELE_LOW("小学校低学年"),
  ELE_HIGH("小学校高学年"),
  JUNIOR_HIGH_PLUS("中学生以上");

  @Schema(description = "DB保存・画面表示に使う日本語ラベル", example = "未就学児")
  private final String value;

  ChildAgeGroup(String value) {
    this.value = value;
  }
}
