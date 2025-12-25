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

  // ログイン未実装のため、MVPでは「固定ユーザー（例：id=1）」としてお気に入りを扱う
  private static final Long DEMO_USER_ID = 1L;

  /**
   * スポット一覧（カテゴリ・設備情報をJOIN済）のデータを取得するメソッド
   *
   * @param condition 検索条件（null項目は条件なしとして扱う）
   * @return スポット一覧（SpotListItemDto のリスト）
   */
  public List<SpotListItemDto> getSpotList(SpotSearchCondition condition) {
    return spotMapper.findAllWithCategoryAndFacilities(condition, DEMO_USER_ID);
    // Mapperに条件＋ユーザーIDを渡して一覧取得
  }

  /**
   * 指定したスポットIDのスポット詳細情報（基本情報＋カテゴリ＋設備＋お気に入り状態）を取得するメソッド
   *
   * @param id スポットID
   * @return スポット詳細データ（SpotDetailDto）
   */
  public SpotDetailDto getSpotDetail(Long id) {
    return spotMapper.findDetailById(id, DEMO_USER_ID);
    // MapperにID＋ユーザーIDを渡して詳細取得
  }

}
