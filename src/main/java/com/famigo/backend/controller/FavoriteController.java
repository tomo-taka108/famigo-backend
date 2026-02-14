package com.famigo.backend.controller;

import com.famigo.backend.dto.SpotListItemDto;
import com.famigo.backend.exception.ErrorResponse;
import com.famigo.backend.security.AppUserPrincipal;
import com.famigo.backend.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * お気に入り（一覧取得 / 追加 / 削除）を提供する REST API の Controller クラスです。
 */
@Tag(name = "お気に入り", description = "お気に入り一覧 / 追加 / 削除")
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

  private final FavoriteService favoriteService;


  @Operation(
      summary = "お気に入り一覧を取得（Read）",
      description = "ログイン中ユーザーのお気に入り（favorites.is_deleted=0）"
          + "かつスポット有効（spots.is_deleted=0）の一覧を返します。"
          + " 並び順は favorites.updated_at DESC（なければ created_at DESC）です。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "お気に入り一覧取得成功",
              content = @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = SpotListItemDto.class))
              )
          ),
          @ApiResponse(
              responseCode = "401",
              description = "未ログイン / トークン不正",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "403",
              description = "権限不足（GUEST等）",
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
  @GetMapping
  public List<SpotListItemDto> getFavorites(@AuthenticationPrincipal AppUserPrincipal principal) {
    Long userId = principal.getUserId();
    return favoriteService.getFavorites(userId);
  }


  @Operation(
      summary = "スポット1件をお気に入り登録（Create/復活）",
      description = "パスで指定されたスポットIDをお気に入り登録します。既に存在し論理削除されている場合は復活します。",
      responses = {
          @ApiResponse(responseCode = "204", description = "お気に入り登録成功（No Content）", content = @Content),
          @ApiResponse(
              responseCode = "401",
              description = "未ログイン / トークン不正",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "403",
              description = "権限不足（GUEST等）",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class)
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
  @PostMapping("/{spotId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void addFavorite(
      @PathVariable Long spotId,
      @AuthenticationPrincipal AppUserPrincipal principal
  ) {
    Long userId = principal.getUserId();
    favoriteService.addFavorite(userId, spotId);
  }


  @Operation(
      summary = "スポット1件をお気に入り解除（論理削除）（Delete）",
      description = "パスで指定されたスポットIDをお気に入り解除（論理削除）します。",
      responses = {
          @ApiResponse(responseCode = "204", description = "お気に入り解除成功（No Content）", content = @Content),
          @ApiResponse(
              responseCode = "401",
              description = "未ログイン / トークン不正",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "403",
              description = "権限不足（GUEST等）",
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
  @DeleteMapping("/{spotId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeFavorite(
      @PathVariable Long spotId,
      @AuthenticationPrincipal AppUserPrincipal principal
  ) {
    Long userId = principal.getUserId();
    favoriteService.removeFavorite(userId, spotId);
  }
}
