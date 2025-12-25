package com.famigo.backend.mapper;

import com.famigo.backend.dto.SpotDetailDto;
import com.famigo.backend.dto.SpotListItemDto;
import com.famigo.backend.dto.SpotSearchCondition;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SpotMapper {

  /**
   * カテゴリ情報＋設備情報＋お気に入り情報を含めたスポット一覧を取得するメソッド （トップページの一覧表示用）
   *
   * @param condition 検索条件
   * @param userId    お気に入り判定対象のユーザーID（ログイン未実装の場合は固定値）
   * @return スポットの一覧（SpotListItemDto のリスト）
   */
  List<SpotListItemDto> findAllWithCategoryAndFacilities(
      @Param("condition") SpotSearchCondition condition,
      @Param("userId") Long userId
  );

  /**
   * 指定したスポットIDの「スポット詳細情報」を取得するメソッド
   *
   * @param id     スポットID
   * @param userId お気に入り判定対象のユーザーID（ログイン未実装の場合は固定値）
   * @return スポット詳細データ（SpotDetailDto）
   */
  SpotDetailDto findDetailById(
      @Param("id") Long id,
      @Param("userId") Long userId
  );

}
