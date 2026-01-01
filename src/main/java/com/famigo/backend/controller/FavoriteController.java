package com.famigo.backend.controller;

import com.famigo.backend.dto.SpotListItemDto;
import com.famigo.backend.security.AppUserPrincipal;
import com.famigo.backend.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 「お気に入り一覧取得」および「スポットのお気に入り登録/解除」を提供する REST API の Controller クラスです。
 * お気に入り一覧画面、およびスポット一覧・詳細画面から呼び出されます。
 */
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class FavoriteController {

  private final FavoriteService favoriteService;


  /**
   * お気に入り一覧（お気に入りしたスポット一覧）を取得するエンドポイント。
   *
   * @param principal ログイン中ユーザー情報（JWTから復元）
   * @return お気に入りスポット一覧（SpotListItemDto のリスト）
   */
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
                  array = @ArraySchema(
                      schema = @Schema(implementation = SpotListItemDto.class)
                  )
              )
          )
          // 将来的に 400 / 404 / 500 などをハンドリングする場合は、ここに ApiResponse を追加していく想定
      }
  )
  @GetMapping("/favorites")
  public List<SpotListItemDto> getFavorites(@AuthenticationPrincipal AppUserPrincipal principal) {
    Long userId = principal.getUserId();
    return favoriteService.getFavorites(userId);
  }


  /**
   * 指定したスポットIDのスポットをお気に入り登録するエンドポイント。
   *
   * @param spotId    対象のスポットID
   * @param principal ログイン中ユーザー情報（JWTから復元）
   */
  @Operation(
      summary = "スポット1件をお気に入り登録（Create/復活）【スポットID指定】",
      description = "パスで指定されたスポットIDをお気に入り登録します。既に存在し論理削除されている場合は復活します。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "お気に入り登録成功"
          )
      }
  )
  @PostMapping("/spots/{spotId}/favorites")
  public void addFavorite(
      @PathVariable Long spotId,
      @AuthenticationPrincipal AppUserPrincipal principal
  ) {
    Long userId = principal.getUserId();
    favoriteService.addFavorite(userId, spotId);
  }


  /**
   * 指定したスポットIDのスポットをお気に入り解除（論理削除）するエンドポイント。
   *
   * @param spotId    対象のスポットID
   * @param principal ログイン中ユーザー情報（JWTから復元）
   */
  @Operation(
      summary = "スポット1件をお気に入り解除（論理削除）【スポットID指定】",
      description = "パスで指定されたスポットIDをお気に入り解除（論理削除）します。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "お気に入り解除成功"
          )
      }
  )
  @DeleteMapping("/spots/{spotId}/favorites")
  public void removeFavorite(
      @PathVariable Long spotId,
      @AuthenticationPrincipal AppUserPrincipal principal
  ) {
    Long userId = principal.getUserId();
    favoriteService.removeFavorite(userId, spotId);
  }

}
