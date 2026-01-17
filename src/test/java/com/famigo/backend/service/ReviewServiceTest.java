package com.famigo.backend.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import com.famigo.backend.dto.ReviewAuthInfoDto;
import com.famigo.backend.dto.ReviewUpsertRequest;
import com.famigo.backend.mapper.ReviewMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * レビューは「他人のレビューを編集できない」＝認可ルールの要。
 * USERとADMINの違いを最低限のテストで押さえる。
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

  @Mock
  private ReviewMapper reviewMapper;

  private ReviewService sut;

  @BeforeEach
  void before() {
    sut = new ReviewService(reviewMapper);
  }

  @Test
  void レビュー更新_USERが他人のレビューを更新しようとすると403になること() {
    ReviewAuthInfoDto auth = new ReviewAuthInfoDto(
       10L,
       1L,
       999L,
       0
    );

    when(reviewMapper.selectReviewAuthInfo(10L)).thenReturn(auth);

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> sut.updateReview(1L, 10L, 1L, "USER", new ReviewUpsertRequest()));

    assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
  }

  @Test
  void レビュー更新_ADMINなら他人のレビューでも403にならないこと() {
    ReviewAuthInfoDto auth = new ReviewAuthInfoDto(
        10L,
        1L,
        999L,
        0
    );

    when(reviewMapper.selectReviewAuthInfo(10L)).thenReturn(auth);
    when(reviewMapper.updateReview(eq(1L), eq(10L), any())).thenReturn(1);

    // ADMINは「他人レビューでも許可」という仕様の確認
    assertDoesNotThrow(() -> sut.updateReview(1L, 10L, 1L, "ADMIN", new ReviewUpsertRequest()));
  }
}
