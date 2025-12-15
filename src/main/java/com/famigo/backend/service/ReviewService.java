package com.famigo.backend.service;

import com.famigo.backend.dto.ReviewListItemDto;
import com.famigo.backend.mapper.ReviewMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor   // finalフィールドを引数に持つコンストラクタを自動生成
public class ReviewService {

  private final ReviewMapper reviewMapper;

  /**
   * スポットIDに紐づくスポット1件のレビュー一覧を投稿日時の新しい順で取得するメソッド
   *
   * @return スポット1件のレビュー一覧
   */
  public List<ReviewListItemDto> getReviewsBySpotId(Long spotId) {
    return reviewMapper.selectReviewsBySpotId(spotId);
  }

}
