package com.famigo.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "スポットの詳細な施設情報を表すデータモデル")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class SpotFacility {

  private Long spotId;
  private Boolean diaperChanging;
  private Boolean strollerOk;
  private Boolean playground;
  private Boolean athletics;
  private Boolean waterPlay;
  private Boolean indoor;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Boolean isDeleted;

}
