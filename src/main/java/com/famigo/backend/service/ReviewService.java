package com.famigo.backend.service;

import com.famigo.backend.dto.ReviewAuthInfoDto;
import com.famigo.backend.dto.ReviewUpsertRequest;
import com.famigo.backend.dto.ReviewListItemDto;
import com.famigo.backend.mapper.ReviewMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor   // finalフィールドを引数に持つコンストラクタを自動生成
public class ReviewService {

  private final ReviewMapper reviewMapper;

  /**
   * スポットIDに紐づくスポット1件のレビュー一覧を投稿日時の新しい順で取得するメソッド
   *
   * @param spotId スポットID
   * @return スポット1件のレビュー一覧
   */
  public List<ReviewListItemDto> getReviewsBySpotId(Long spotId) {
    return reviewMapper.selectReviewsBySpotId(spotId);
  }


  /**
   * スポットIDとユーザーIDを紐づけてレビューを新規登録するメソッド
   *
   * @param spotId  スポットID
   * @param userId  ユーザーID（ログインユーザー）
   * @param request レビュー投稿リクエストDTO
   */
  @Transactional
  public void createReview(Long spotId, Long userId, ReviewUpsertRequest request) {
    reviewMapper.insertReview(spotId, userId, request);
  }


  /**
   * 指定レビューを編集（Update）するメソッド。
   * 認可ルール：
   *  - USER：自分のレビューのみ編集可能
   *  - ADMIN：将来の運用を見据えて、全レビューを編集可能（※不要ならここで弾く）
   *
   * @param spotId   スポットID（URLパス）
   * @param reviewId レビューID（URLパス）
   * @param userId   ログインユーザーID
   * @param role     ログインユーザーロール（USER / ADMIN）
   * @param request  レビュー編集リクエストDTO
   */
  @Transactional
  public void updateReview(Long spotId, Long reviewId, Long userId, String role, ReviewUpsertRequest request) {

    ReviewAuthInfoDto authInfo = reviewMapper.selectReviewAuthInfo(reviewId);

    // 存在しない / 論理削除済みは 404
    if (authInfo == null || authInfo.getIsDeleted() == 1) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found: id=" + reviewId);
    }

    // URL の spotId と DB の spotId が一致しない場合も 404（スポット跨ぎを防止）
    if (!authInfo.getSpotId().equals(spotId)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "Review not found for spotId=" + spotId + ", reviewId=" + reviewId
      );
    }

    // 自分のレビュー以外を編集しようとしたら 403（ただし ADMIN は許可）
    boolean isAdmin = "ADMIN".equals(role);
    if (!isAdmin && !authInfo.getUserId().equals(userId)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          "Access denied: cannot edit others' review. reviewId=" + reviewId
      );
    }

    int updated = reviewMapper.updateReview(spotId, reviewId, request);

    // 念のため、更新対象が消えていた場合（並行更新など）は 404
    if (updated == 0) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found: id=" + reviewId);
    }
  }


  /**
   * 指定レビューを論理削除（Delete）するメソッド。
   * 認可ルール：
   *  - USER：自分のレビューのみ削除可能
   *  - ADMIN：将来の運用を見据えて、全レビューを削除可能（※不要ならここで弾く）
   *
   * @param spotId   スポットID（URLパス）
   * @param reviewId レビューID（URLパス）
   * @param userId   ログインユーザーID
   * @param role     ログインユーザーロール（USER / ADMIN）
   */
  @Transactional
  public void deleteReview(Long spotId, Long reviewId, Long userId, String role) {

    ReviewAuthInfoDto authInfo = reviewMapper.selectReviewAuthInfo(reviewId);

    // 存在しない / 論理削除済みは 404
    if (authInfo == null || authInfo.getIsDeleted() == 1) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found: id=" + reviewId);
    }

    // URL の spotId と DB の spotId が一致しない場合も 404（スポット跨ぎを防止）
    if (!authInfo.getSpotId().equals(spotId)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "Review not found for spotId=" + spotId + ", reviewId=" + reviewId
      );
    }

    // 自分のレビュー以外を削除しようとしたら 403（ただし ADMIN は許可）
    boolean isAdmin = "ADMIN".equals(role);
    if (!isAdmin && !authInfo.getUserId().equals(userId)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          "Access denied: cannot delete others' review. reviewId=" + reviewId
      );
    }

    int deleted = reviewMapper.softDeleteReview(spotId, reviewId);

    // 念のため、削除対象が消えていた場合（並行更新など）は 404
    if (deleted == 0) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found: id=" + reviewId);
    }
  }

}
