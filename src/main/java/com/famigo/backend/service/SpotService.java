package com.famigo.backend.service;

import com.famigo.backend.dto.SpotDetailDto;
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
   * スポット一覧（カテゴリ・設備情報をJOIN済）のデータを取得するメソッド
   *
   * @return スポット一覧（SpotListItemのリスト）
   */
  public List<SpotListItemDto> getSpotList() {
    return spotMapper.findAllWithCategoryAndFacilities();
    // Mapper経由でJOIN結果を取得し、そのまま返却
  }

  /**
   * 指定したスポットIDのスポット詳細情報（基本情報＋カテゴリ＋設備情報等）を取得するメソッド
   *
   * @param id スポットID
   * @return スポット詳細データ（SpotDetailDto）
   */
  public SpotDetailDto getSpotDetail(Long id) {
    return spotMapper.findDetailById(id);
    // Mapper経由でJOIN結果を取得し、そのまま返却
  }

}
