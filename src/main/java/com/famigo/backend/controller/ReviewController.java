package com.famigo.backend.controller;

import com.famigo.backend.dto.ReviewCreateRequest;
import com.famigo.backend.dto.ReviewListItemDto;
import com.famigo.backend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 「スポット一件のレビュー一覧」を提供する REST API の Controller クラスです。 スポット詳細画面から呼び出されます。
 */
@RestController
@RequestMapping("/spots")   // このコントローラーで扱うURLの共通プレフィックスを設定
@CrossOrigin(origins = "http://localhost:5173")    // Viteのフロントからのアクセスを許可
@RequiredArgsConstructor    // finalフィールドを引数に持つコンストラクタを自動生成
public class ReviewController {

  private final ReviewService reviewService;

  /**
   * 指定したスポットIDのスポットに関するレビュー一覧を取得するエンドポイント。
   *
   * @param spotId 取得対象のスポットID
   * @return List<ReviewListItemDto>（スポット１件のレビュー一覧）
   */
  @Operation(
      summary = "スポット1件についてレビュー一覧の取得【スポットID指定】",
      description = "パスで指定されたスポットIDに該当するスポットについてのレビュー一覧を取得します。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "レビュー取得成功",
              content = @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(
                      schema = @Schema(implementation = ReviewListItemDto.class)
                  )
              )
          )
          // 将来的に 400 / 404 / 500 などをハンドリングする場合は、
          // ここに ApiResponse を追加していく想定
      }
  )

  @GetMapping("/{spotId}/reviews")
  public List<ReviewListItemDto> getReviews(@PathVariable Long spotId) {
    return reviewService.getReviewsBySpotId(spotId);
  }


  /**
   * 指定したスポットIDのスポットに対してレビューを新規投稿するエンドポイント。
   *
   * @param spotId  投稿対象のスポットID
   * @param request レビュー投稿内容（バリデーション対象）
   */
  @Operation(
      summary = "スポット1件についてレビュー投稿（Create）【スポットID指定】",
      description = "パスで指定されたスポットIDに対してレビューを新規登録します。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "レビュー投稿成功"
          )
          // 将来的に 400 / 404 / 500 などをハンドリングする場合は、
          // ここに ApiResponse を追加していく想定
      }
  )
  @PostMapping("/{spotId}/reviews")
  public void createReview(
      @PathVariable Long spotId,
      @RequestBody @Valid ReviewCreateRequest request
  ) {
    reviewService.createReview(spotId, request);
  }

}
