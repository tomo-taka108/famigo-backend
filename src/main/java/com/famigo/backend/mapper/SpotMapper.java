package com.famigo.backend.mapper;

import com.famigo.backend.dto.SpotListItemDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SpotMapper {

  /**
   * カテゴリ情報＋設備情報を含めたスポット一覧を取得するメソッド
   * （トップページの一覧表示用）
   *
   * @return SpotListItemDto のリスト（スポットの一覧）
   */
  List<SpotListItemDto> findAllWithCategoryAndFacilities();

}
