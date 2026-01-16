package com.famigo.backend.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.famigo.backend.dto.ReviewAuthInfoDto;
import com.famigo.backend.dto.ReviewListItemDto;
import com.famigo.backend.dto.ReviewUpsertRequest;
import com.famigo.backend.testsupport.MybatisTestBase;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ReviewMapper は「一覧表示」と「編集/削除の認可判定」に直結する。
 * - 一覧で childAgeGroup が取れるか（フロント要件）
 * - 認可情報（誰のレビューか）が取れるか
 */
@MybatisTest
class ReviewMapperTest extends MybatisTestBase {

  @Autowired
  private ReviewMapper sut;

  @Test
  void スポットIDに紐づくレビュー一覧が取得できること() {
    // seed前提：spotId=2 が存在する想定（違う場合はseedに合わせてID変更）
    List<ReviewListItemDto> result = sut.selectReviewsBySpotId(2L);

    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();

    // フロント表示で空になると困る項目（最低限ここだけチェック）
    assertThat(result.get(0).getChildAgeGroup()).isNotNull();
  }

  @Test
  void レビュー認可情報が取得できること() {
    // seed前提：reviewId=1 が存在する想定（違う場合はseedのIDに合わせて変更）
    ReviewAuthInfoDto auth = sut.selectReviewAuthInfo(1L);

    assertThat(auth).isNotNull();
    assertThat(auth.getId()).isNotNull();
    assertThat(auth.getUserId()).isNotNull();
    assertThat(auth.getSpotId()).isNotNull();
  }

  @Test
  void レビュー更新ができること_更新対象が存在する場合() {
    // seed前提：spotId=2 / reviewId=1 が存在する想定（違う場合はseedに合わせてID変更）
    ReviewUpsertRequest req = new ReviewUpsertRequest();
    req.setRating(1);
    req.setReviewText("レビューを更新しました。これはテストです。");
    req.setCostTotal(5000);

    int updated = sut.updateReview(2L, 1L, req);

    // 1件更新されること（SQLのwhere条件が効いているかの最低確認）
    assertThat(updated).isEqualTo(1);
  }
}
