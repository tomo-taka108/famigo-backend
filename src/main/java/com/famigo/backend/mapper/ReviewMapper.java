package com.famigo.backend.mapper;

import com.famigo.backend.dto.ReviewCreateRequest;
import com.famigo.backend.dto.ReviewListItemDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewMapper {

  /**
   * スポットIDに紐づくスポット1件のレビュー一覧を取得するメソッド
   *
   * @param spotId スポットID
   * @return スポット1件のレビュー一覧
   */
  List<ReviewListItemDto> selectReviewsBySpotId(Long spotId);

  /**
   * スポットに紐づくレビューを新規登録するメソッド
   *
   * @param spotId スポットID
   * @param userId ユーザーID
   * @param request レビュー投稿リクエストDTO
   */
  void insertReview(
      @Param("spotId") Long spotId,
      @Param("userId") Long userId,
      @Param("request") ReviewCreateRequest request
  );

}
