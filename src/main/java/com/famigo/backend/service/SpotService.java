package com.famigo.backend.service;

import com.famigo.backend.dto.SpotListItemDto;
import com.famigo.backend.mapper.SpotMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor   // finalフィールドを引数に持つコンストラクタを自動生成
public class SpotService {

  private final SpotMapper spotMapper;

  /**
   * スポット一覧（カテゴリ・設備情報をJOIN済）を取得するメソッド
   *
   * @return スポット1件の設備情報
   */
  public List<SpotListItemDto> getSpotList() {
    return spotMapper.findAllWithCategoryAndFacilities();
    // Mapper経由でJOIN結果を取得し、そのまま返却
  }
}
