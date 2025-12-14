package com.famigo.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "カテゴリ情報を表すデータモデル")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CategoryDto {

  @Schema(description = "カテゴリID（categories.id）", example = "1")
  private Long id;

  @Schema(description = "カテゴリ名（categories.name）", example = "公園")
  private String name;

}
