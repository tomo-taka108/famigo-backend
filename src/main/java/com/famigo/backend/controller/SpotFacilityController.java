package com.famigo.backend.controller;

import com.famigo.backend.entity.SpotFacility;
import com.famigo.backend.service.SpotFacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spot-facilities")   // このコントローラーで扱うURLの共通プレフィックスを設定
@RequiredArgsConstructor              // finalフィールドを引数に持つコンストラクタを自動生成
public class SpotFacilityController {

  private final SpotFacilityService spotFacilityService;

  @GetMapping("/{spotId}")
  public ResponseEntity<SpotFacility> getSpotFacility(
      @PathVariable Long spotId) {
    SpotFacility facility = spotFacilityService.getSpotFacilityBySpotId(spotId);

    if (facility == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(facility);
  }

}
