package com.famigo.backend.controller;

import com.famigo.backend.dto.SpotDetailDto;
import com.famigo.backend.dto.SpotListItemDto;
import com.famigo.backend.dto.SpotSearchCondition;
import com.famigo.backend.service.SpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
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
   * スポット一覧（カテゴリ・設備情報を JOIN 済）のデータを取得するエンドポイントです。 検索条件（カテゴリ、住所、価格帯、対象年齢、設備など）は任意で指定可能です。
   * いずれの条件も指定されない場合は、論理削除されていないスポットを全件取得します。
   *
   * @param categoryIds カテゴリIDのリスト（例：?categoryIds=1&categoryIds=3）
   * @param address     住所（部分一致検索用の文字列）
   * @param price       価格帯のリスト（free / 1000 / 2000 / paid）
   * @param age         対象年齢のリスト（toddler / elementary）
   * @param facilities  設備条件のリスト（toilet / parking / diaper / indoor / water / largePlayground /
   *                    stroller など）
   * @return 条件に合致するスポット一覧（SpotListItemDto のリスト）
   */
  @Operation(
      summary = "スポット一覧の取得【検索条件指定可】",
      description = "カテゴリ情報・設備情報を JOIN 済みのスポット一覧を取得します。"
          + "クエリパラメータでカテゴリID、住所、価格帯、対象年齢、設備などを指定可能です。"
          + "いずれの条件も指定されない場合は、論理削除されていないスポットを ID 昇順で全件返します。",
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
      @RequestParam(required = false) String address,
      @RequestParam(required = false) List<String> price,
      @RequestParam(required = false) List<String> age,
      @RequestParam(required = false, name = "facilities") List<String> facilities
  ) {

    // パラメータが指定されない場合は null になる想定
    // → Service / Mapper 側で「null なら条件なし」として扱う
    SpotSearchCondition condition = new SpotSearchCondition();
    condition.setCategoryIds(categoryIds);
    condition.setAddress(address);
    condition.setPrice(price);
    condition.setAge(age);
    condition.setFacilities(facilities);

    return spotService.getSpotList(condition);
  }


  /**
   * 指定したスポットIDの詳細情報（基本情報 + カテゴリ + 設備情報など）を取得するエンドポイントです。
   *
   * @param id 取得対象のスポットID
   * @return SpotDetailDto（スポット詳細情報）
   */
  @Operation(
      summary = "スポット詳細情報の取得【スポットID指定】",
      description = "パスで指定されたスポットIDに該当するスポット詳細情報を1件取得します。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "詳細取得成功",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SpotDetailDto.class)
              )
          )
          // 将来的に 400 / 404 / 500 などをハンドリングする場合は、
          // ここに ApiResponse を追加していく想定
      }
  )

  @GetMapping("/{id}")
  public SpotDetailDto getSpotDetail(@PathVariable Long id) {
    return spotService.getSpotDetail(id);
  }

}
