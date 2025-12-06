package com.famigo.backend.controller;

import com.famigo.backend.dto.SpotListItemDto;
import com.famigo.backend.service.SpotService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spots")   // このコントローラーで扱うURLの共通プレフィックスを設定
@RequiredArgsConstructor    // finalフィールドを引数に持つコンストラクタを自動生成
public class SpotController {

  private final SpotService spotService;

  @GetMapping
  public List<SpotListItemDto> getSpots() {

    return spotService.getSpotList();
  }

}
