package com.famigo.backend.controller;

import com.famigo.backend.dto.ReviewUpsertRequest;
import com.famigo.backend.dto.ReviewListItemDto;
import com.famigo.backend.security.AppUserPrincipal;
import com.famigo.backend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 「スポット一件のレビュー一覧」を提供する REST API の Controller クラスです。スポット詳細画面から呼び出されます。
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
      summary = "レビュー一覧取得【スポットID指定】",
      description = "パスで指定されたスポットIDに紐づくレビュー一覧を取得します。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "レビュー一覧取得成功",
              content = @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(
                      schema = @Schema(implementation = ReviewListItemDto.class)
                  )
              )
          )
          // 将来的に 400 / 404 / 500 などをハンドリングする場合は、ここに ApiResponse を追加していく想定
      }
  )

  @GetMapping("/{spotId}/reviews")
  public List<ReviewListItemDto> getReviewsBySpotId(@PathVariable Long spotId) {
    return reviewService.getReviewsBySpotId(spotId);
  }


  /**
   * 指定したスポットIDのスポットにレビューを新規投稿するエンドポイント。
   *
   * @param spotId  投稿対象のスポットID
   * @param request レビュー投稿リクエストDTO（バリデーション対象）
   * @param principal ログイン中ユーザー情報（JWTから復元）
   */
  @Operation(
      summary = "スポット1件についてレビュー投稿【スポットID指定】",
      description = "パスで指定されたスポットIDに対して、ログイン中ユーザーがレビューを投稿します。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "レビュー投稿成功"
          )
          // 将来的に 400 / 404 / 500 などをハンドリングする場合は、ここに ApiResponse を追加していく想定
      }
  )
  @PostMapping("/{spotId}/reviews")
  public void createReview(
      @PathVariable Long spotId,
      @RequestBody @Valid ReviewUpsertRequest request,
      @AuthenticationPrincipal AppUserPrincipal principal
  ) {
    Long userId = principal.getUserId();
    reviewService.createReview(spotId, userId, request);
  }


  /**
   * 指定したスポットID配下のレビューを編集するエンドポイント。
   *
   * @param spotId スポットID
   * @param reviewId レビューID
   * @param request レビュー編集リクエストDTO
   * @param principal ログインユーザー情報（JWTから復元）
   */
  @Operation(
      summary = "レビュー編集【スポットID + レビューID指定】",
      description = "パスで指定されたスポットID配下のレビューを編集します（本人のみ。将来はADMIN例外）。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "レビュー編集成功"
          )
          // 400 / 401 / 403 / 404 / 500 は共通ハンドリング（GlobalExceptionHandler / Security）
      }
  )
  @PutMapping("/{spotId}/reviews/{reviewId}")
  public void updateReview(
      @PathVariable Long spotId,
      @PathVariable Long reviewId,
      @RequestBody @Valid ReviewUpsertRequest request,
      @AuthenticationPrincipal AppUserPrincipal principal
  ) {
    reviewService.updateReview(
        spotId,
        reviewId,
        principal.getUserId(),
        principal.getRole(),
        request
    );
  }


  /**
   * 指定したスポットID配下のレビューを削除（論理削除）するエンドポイント。
   *
   * @param spotId スポットID
   * @param reviewId レビューID
   * @param principal ログインユーザー情報（JWTから復元）
   */
  @Operation(
      summary = "レビュー削除【スポットID + レビューID指定】",
      description = "パスで指定されたスポットID配下のレビューを削除します（論理削除）。本人のみ。将来はADMIN例外。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "レビュー削除成功"
          )
          // 401 / 403 / 404 / 500 は共通ハンドリング（GlobalExceptionHandler / Security）
      }
  )
  @DeleteMapping("/{spotId}/reviews/{reviewId}")
  public void deleteReview(
      @PathVariable Long spotId,
      @PathVariable Long reviewId,
      @AuthenticationPrincipal AppUserPrincipal principal
  ) {
    reviewService.deleteReview(
        spotId,
        reviewId,
        principal.getUserId(),
        principal.getRole()
    );
  }

}
