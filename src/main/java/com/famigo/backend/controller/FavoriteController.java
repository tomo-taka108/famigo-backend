package com.famigo.backend.controller;

import com.famigo.backend.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 「スポットのお気に入り登録/解除」を提供する REST API の Controller クラスです。
 * スポット一覧・詳細画面から呼び出されます。
 */
@RestController
@RequestMapping("/spots")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class FavoriteController {

  private final FavoriteService favoriteService;

  /**
   * 指定したスポットIDのスポットをお気に入り登録するエンドポイント。
   *
   * @param spotId 対象のスポットID
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
  @PostMapping("/{spotId}/favorites")
  public void addFavorite(@PathVariable Long spotId) {
    favoriteService.addFavorite(spotId);
  }

  /**
   * 指定したスポットIDのスポットをお気に入り解除（論理削除）するエンドポイント。
   *
   * @param spotId 対象のスポットID
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
  @DeleteMapping("/{spotId}/favorites")
  public void removeFavorite(@PathVariable Long spotId) {
    favoriteService.removeFavorite(spotId);
  }

}
