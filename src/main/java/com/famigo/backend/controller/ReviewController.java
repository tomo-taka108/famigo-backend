package com.famigo.backend.controller;

import com.famigo.backend.dto.ReviewUpsertRequest;
import com.famigo.backend.dto.ReviewListItemDto;
import com.famigo.backend.exception.ErrorResponse;
import com.famigo.backend.security.AppUserPrincipal;
import com.famigo.backend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 「スポット一件のレビュー一覧」を提供する REST API の Controller クラスです。スポット詳細画面から呼び出されます。
 */
@Tag(name = "レビュー", description = "スポットのレビュー一覧 / 投稿 / 編集 / 削除")
@RestController
@RequestMapping("/api/spots")
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;


  @Operation(
      summary = "レビュー一覧取得【スポットID指定】",
      description = "パスで指定されたスポットIDに紐づくレビュー一覧を取得します。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "レビュー一覧取得成功",
              content = @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = ReviewListItemDto.class))
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "スポットが存在しない",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "500",
              description = "想定外エラー",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class)
              )
          )
      }
  )
  @GetMapping("/{spotId}/reviews")
  public List<ReviewListItemDto> getReviewsBySpotId(@PathVariable Long spotId) {
    return reviewService.getReviewsBySpotId(spotId);
  }


  @Operation(
      summary = "スポット1件についてレビュー投稿【スポットID指定】",
      description = "パスで指定されたスポットIDに対して、ログイン中ユーザーがレビューを投稿します。",
      responses = {
          @ApiResponse(responseCode = "204", description = "レビュー投稿成功（No Content）", content = @Content),
          @ApiResponse(
              responseCode = "400",
              description = "入力不正（バリデーションエラー）",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
          ),
          @ApiResponse(
              responseCode = "401",
              description = "未ログイン / トークン不正",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
          ),
          @ApiResponse(
              responseCode = "403",
              description = "権限不足（GUEST等）",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "スポットが存在しない",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
          ),
          @ApiResponse(
              responseCode = "500",
              description = "想定外エラー",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
          )
      }
  )
  @PostMapping("/{spotId}/reviews")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void createReview(
      @PathVariable Long spotId,
      @RequestBody @Valid ReviewUpsertRequest request,
      @AuthenticationPrincipal AppUserPrincipal principal
  ) {
    reviewService.createReview(spotId, principal.getUserId(), request);
  }


  @Operation(
      summary = "レビュー編集【スポットID + レビューID指定】",
      description = "パスで指定されたスポットID配下のレビューを編集します（本人のみ。将来はADMIN例外）。",
      responses = {
          @ApiResponse(responseCode = "204", description = "レビュー編集成功（No Content）", content = @Content),
          @ApiResponse(
              responseCode = "400",
              description = "入力不正（バリデーションエラー）",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
          ),
          @ApiResponse(
              responseCode = "401",
              description = "未ログイン / トークン不正",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
          ),
          @ApiResponse(
              responseCode = "403",
              description = "権限不足（本人以外など）",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "スポット or レビューが存在しない",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
          ),
          @ApiResponse(
              responseCode = "500",
              description = "想定外エラー",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
          )
      }
  )
  @PutMapping("/{spotId}/reviews/{reviewId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
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


  @Operation(
      summary = "レビュー削除【スポットID + レビューID指定】",
      description = "パスで指定されたスポットID配下のレビューを削除します（論理削除）。本人のみ。将来はADMIN例外。",
      responses = {
          @ApiResponse(responseCode = "204", description = "レビュー削除成功（No Content）", content = @Content),
          @ApiResponse(
              responseCode = "401",
              description = "未ログイン / トークン不正",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
          ),
          @ApiResponse(
              responseCode = "403",
              description = "権限不足（本人以外など）",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "スポット or レビューが存在しない",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
          ),
          @ApiResponse(
              responseCode = "500",
              description = "想定外エラー",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
          )
      }
  )
  @DeleteMapping("/{spotId}/reviews/{reviewId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
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
