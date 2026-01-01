package com.famigo.backend.controller;

import com.famigo.backend.dto.SpotDetailDto;
import com.famigo.backend.dto.SpotListItemDto;
import com.famigo.backend.dto.SpotSearchCondition;
import com.famigo.backend.enums.AgeGroup;
import com.famigo.backend.enums.PriceType;
import com.famigo.backend.security.AppUserPrincipal;
import com.famigo.backend.service.SpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;   // ★ CORS許可用アノテーションをインポート
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 「スポット一覧・詳細情報」を提供する REST API の Controller クラスです。 トップページ一覧・スポット詳細画面から呼び出されます。
 */
@RestController
@RequestMapping("/spots")   // このコントローラーで扱うURLの共通プレフィックスを設定
@CrossOrigin(origins = "http://localhost:5173")    // Viteのフロントからのアクセスを許可
@RequiredArgsConstructor    // finalフィールドを引数に持つコンストラクタを自動生成
public class SpotController {

  private final SpotService spotService;

  /**
   * スポット一覧（カテゴリ・設備情報を JOIN 済）のデータを取得するエンドポイント。 検索条件（カテゴリ、キーワード、予算、対象年齢、設備など）は任意で指定可能。
   * いずれの条件も指定されない場合は、論理削除されていないスポットを全件取得。
   *
   * @param categoryIds カテゴリIDのリスト（複数指定可）
   * @param keyword     キーワード（スポット名/住所/エリアをまとめて部分一致検索）
   * @param price       予算（Enum名を指定：FREE / UNDER_1000 / UNDER_2000 / OVER_2000）（複数指定可）
   * @param age         対象年齢（Enum名を指定：ALL / PRESCHOOL / ELE_LOW / ELE_HIGH / JUNIOR_HIGH）（複数指定可）
   * @param facilities  設備フィルタ（例：diaper, stroller, playground, athletics, water, indoor）（複数指定可）
   * @param principal   ログイン中ユーザー情報（未ログインの場合はnull）
   * @return 条件に合致するスポット一覧（SpotListItemDto のリスト）
   */
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
                  array = @ArraySchema(
                      schema = @Schema(implementation = SpotListItemDto.class)
                  )
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

    // ★ 検索条件DTOを組み立て（nullなら条件なしとして扱う）
    SpotSearchCondition condition = new SpotSearchCondition();
    condition.setCategoryIds(categoryIds);
    condition.setKeyword(keyword);
    condition.setPrice(price);
    condition.setAge(age);
    condition.setFacilities(facilities);
    Long userId = resolveUserId(principal);

    return spotService.getSpotList(condition, userId);
  }


  /**
   * 指定したスポットIDの詳細情報（基本情報 + カテゴリ + 設備情報など）を取得するエンドポイント。
   *
   * @param id        取得対象のスポットID
   * @param principal ログイン中ユーザー情報（未ログインの場合はnull）
   * @return SpotDetailDto（スポット詳細情報）
   */
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
          )
          // 将来的に 400 / 404 / 500 などをハンドリングする場合は、ここに ApiResponse を追加していく想定
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


  /**
   * @param principal Authentication#getPrincipal() 相当のオブジェクト
   * @return ログイン中なら userId、未ログインなら null
   */
  private Long resolveUserId(Object principal) {
    if (principal instanceof AppUserPrincipal appUserPrincipal) {
      return appUserPrincipal.getUserId();
    }
    return null;
  }

}
