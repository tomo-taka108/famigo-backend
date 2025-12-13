package com.famigo.backend.service;

import com.famigo.backend.dto.SpotDetailDto;
import com.famigo.backend.dto.SpotListItemDto;
import com.famigo.backend.dto.SpotSearchCondition;
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
   * @param condition 検索条件（null項目は条件なしとして扱う）
   * @return スポット一覧（SpotListItemDto のリスト）
   */
  public List<SpotListItemDto> getSpotList(SpotSearchCondition condition) {
    return spotMapper.findAllWithCategoryAndFacilities(condition);
    // Mapperに条件を渡して一覧取得
  }

  /**
   * 指定したスポットIDのスポット詳細情報（基本情報＋カテゴリ＋設備情報等）を取得するメソッド
   *
   * @param id スポットID
   * @return スポット詳細データ（SpotDetailDto）
   */
  public SpotDetailDto getSpotDetail(Long id) {
    return spotMapper.findDetailById(id);
    // MapperにIDを渡して詳細取得
  }

}
