package com.famigo.backend.mapper;

import com.famigo.backend.dto.ReviewAuthInfoDto;
import com.famigo.backend.dto.ReviewUpsertRequest;
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
  List<ReviewListItemDto> selectReviewsBySpotId(@Param("spotId") Long spotId);


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
      @Param("request") ReviewUpsertRequest request
  );


  /**
   * レビュー編集・削除の認可判定に必要な情報（spotId/userId/isDeleted）を取得するメソッド
   *
   * @param reviewId レビューID
   * @return 認可判定用DTO（存在しない場合は null）
   */
  ReviewAuthInfoDto selectReviewAuthInfo(@Param("reviewId") Long reviewId);


  /**
   * 指定レビューを更新するメソッド（論理削除済みは更新しない）
   *
   * @param spotId スポットID（URLパス）
   * @param reviewId レビューID（URLパス）
   * @param request レビュー編集リクエストDTO
   * @return 更新件数（0の場合は対象なし）
   */
  int updateReview(
      @Param("spotId") Long spotId,
      @Param("reviewId") Long reviewId,
      @Param("request") ReviewUpsertRequest request
  );


  /**
   * 指定レビューを論理削除するメソッド（既に削除済みは更新しない）
   *
   * @param spotId スポットID（URLパス）
   * @param reviewId レビューID（URLパス）
   * @return 更新件数（0の場合は対象なし）
   */
  int softDeleteReview(
      @Param("spotId") Long spotId,
      @Param("reviewId") Long reviewId
  );

}
