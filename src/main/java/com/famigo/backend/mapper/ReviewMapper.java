package com.famigo.backend.mapper;

import com.famigo.backend.dto.ReviewListItemDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReviewMapper {

  /**
   * スポットIDに紐づくスポット1件のレビュー一覧を取得するメソッド
   *
   * @param spotId スポットID
   * @return スポット1件のレビュー一覧
   */
  List<ReviewListItemDto> selectReviewsBySpotId(Long spotId);

}
