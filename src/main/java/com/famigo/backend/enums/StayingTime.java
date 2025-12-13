package com.famigo.backend.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "滞在時間の目安（スポット検索条件や表示に利用する）")
@Getter

public enum StayingTime {

  WITHIN_1H("1時間以内"),
  WITHIN_2H("2時間以内"),
  HALF_DAY("半日"),
  FULL_DAY("1日");

  @Schema(description = "画面表示・DB保存に使う値（日本語ラベル）", example = "1時間以内")
  private final String value;

  StayingTime(String value) {
    this.value = value;
  }
}
