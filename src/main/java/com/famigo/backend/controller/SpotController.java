package com.famigo.backend.controller;

import com.famigo.backend.dto.SpotDetailDto;
import com.famigo.backend.dto.SpotListItemDto;
import com.famigo.backend.dto.SpotSearchCondition;
import com.famigo.backend.exception.ErrorResponse;
import com.famigo.backend.enums.AgeGroup;
import com.famigo.backend.enums.PriceType;
import com.famigo.backend.security.AppUserPrincipal;
import com.famigo.backend.service.SpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 「スポット一覧・詳細情報」を提供する REST API の Controller クラスです。 トップページ一覧・スポット詳細画面から呼び出されます。
 */
@RestController
@Tag(name = "スポット", description = "スポット一覧 / 詳細")
@RequestMapping("/api/spots")
@RequiredArgsConstructor
public class SpotController {

  private final SpotService spotService;

  @Operation(
      summary = "スポット一覧の取得【検索条件指定可】",
      description = "カテゴリ情報・設備情報を JOIN 済みのスポット一覧を取得します。"
          + "クエリパラメータでカテゴリID、キーワード、予算（Enum名）、対象年齢（Enum名）、設備などを指定可能です。"
          + "いずれの条件も指定されない場合は、論理削除されていないスポットを ID 昇順で全件返します。"
          + "ログイン時は、お気に入り状態（isFavorite）も合わせて返します。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "一覧取得成功",
              content = @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = SpotListItemDto.class))
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "入力不正（クエリパラメータのバリデーションエラー等）",
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
  public List<SpotListItemDto> getSpots(
      @RequestParam(required = false) List<Long> categoryIds,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) List<PriceType> price,
      @RequestParam(required = false) List<AgeGroup> age,
      @RequestParam(required = false, name = "facilities") List<String> facilities,
      @AuthenticationPrincipal Object principal
  ) {
    SpotSearchCondition condition = new SpotSearchCondition();
    condition.setCategoryIds(categoryIds);
    condition.setKeyword(keyword);
    condition.setPrice(price);
    condition.setAge(age);
    condition.setFacilities(facilities);

    Long userId = resolveUserId(principal);
    return spotService.getSpotList(condition, userId);
  }


  @Operation(
      summary = "スポット詳細情報の取得【スポットID指定】",
      description = "パスで指定されたスポットIDに該当するスポット詳細情報を1件取得します。"
          + "ログイン時は、お気に入り状態（isFavorite）も合わせて返します。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "詳細取得成功",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SpotDetailDto.class)
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
  @GetMapping("/{id}")
  public SpotDetailDto getSpotDetail(
      @PathVariable Long id,
      @AuthenticationPrincipal Object principal
  ) {
    Long userId = resolveUserId(principal);
    return spotService.getSpotDetail(id, userId);
  }

  private Long resolveUserId(Object principal) {
    if (principal instanceof AppUserPrincipal appUserPrincipal) {
      return appUserPrincipal.getUserId();
    }
    return null;
  }
}
