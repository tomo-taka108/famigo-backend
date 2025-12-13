package com.famigo.backend.mapper;

import com.famigo.backend.dto.SpotDetailDto;
import com.famigo.backend.dto.SpotListItemDto;
import com.famigo.backend.dto.SpotSearchCondition;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SpotMapper {

  /**
   * カテゴリ情報＋設備情報を含めたスポット一覧を取得するメソッド
   * （トップページの一覧表示用）
   *
   * @return スポットの一覧（SpotListItemDto のリスト）
   */
  List<SpotListItemDto> findAllWithCategoryAndFacilities(SpotSearchCondition condition);

  /**
   * 指定したスポットIDの「スポット詳細情報」を取得するメソッド
   * （スポット詳細画面向け：基本情報＋カテゴリ＋設備＋レビュー情報など。レビューは今後拡張予定）
   *
   * @param id スポットID
   * @return スポット詳細データ（SpotDetailDto）
   */
  SpotDetailDto findDetailById(Long id);

}
