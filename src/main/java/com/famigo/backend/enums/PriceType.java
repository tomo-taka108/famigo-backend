package com.famigo.backend.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "料金区分（スポット検索条件や表示に利用する）")
@Getter

public enum PriceType {

  FREE("無料"),
  UNDER_1000("1000円以内"),
  UNDER_2000("2000円以内"),
  OVER_2000("2000円以上");

  @Schema(description = "画面表示・DB保存に使う値（日本語ラベル）", example = "1000円以内")
  private final String value;

  PriceType(String value) {
    this.value = value;
  }
}
