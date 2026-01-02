package com.famigo.backend.service;

import com.famigo.backend.dto.SpotDetailDto;
import com.famigo.backend.dto.SpotListItemDto;
import com.famigo.backend.dto.SpotSearchCondition;
import com.famigo.backend.mapper.SpotMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor   // finalフィールドを引数に持つコンストラクタを自動生成
public class SpotService {

  private final SpotMapper spotMapper;

  /**
   * スポット一覧（カテゴリ・設備情報をJOIN済）のデータを取得するメソッド
   *
   * @param condition 検索条件（null項目は条件なしとして扱う）
   * @param userId    ユーザーID（ログイン時のみ指定。未ログインの場合はnullを渡す）
   * @return スポット一覧（SpotListItemDto のリスト）
   */
  public List<SpotListItemDto> getSpotList(SpotSearchCondition condition, Long userId) {
    return spotMapper.findAllWithCategoryAndFacilities(condition, userId);
    // Mapperに条件＋（ログイン時のみ）ユーザーIDを渡して一覧取得
  }

  /**
   * 指定したスポットIDのスポット詳細情報（基本情報＋カテゴリ＋設備＋お気に入り状態）を取得するメソッド
   *
   * @param id     スポットID
   * @param userId ユーザーID（ログイン時のみ指定。未ログインの場合はnullを渡す）
   * @return スポット詳細データ（SpotDetailDto）
   */
  public SpotDetailDto getSpotDetail(Long id, Long userId) {

    SpotDetailDto dto = spotMapper.findDetailById(id, userId);
    // MapperにID＋（ログイン時のみ）ユーザーIDを渡して詳細取得

    if (dto == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Spot not found: id=" + id);
    }

    return dto;
  }

}
