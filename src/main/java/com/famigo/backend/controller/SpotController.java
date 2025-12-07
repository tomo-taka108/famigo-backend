package com.famigo.backend.controller;

import com.famigo.backend.dto.SpotDetailDto;
import com.famigo.backend.dto.SpotListItemDto;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * 「スポット一覧・詳細情報」を提供する REST API の Controller クラスです。
 * トップページ一覧・スポット詳細画面から呼び出されます。
 */
@RestController
@RequestMapping("/spots")   // このコントローラーで扱うURLの共通プレフィックスを設定
@CrossOrigin(origins = "http://localhost:5173")    // Viteのフロントからのアクセスを許可
@RequiredArgsConstructor    // finalフィールドを引数に持つコンストラクタを自動生成
public class SpotController {

  private final SpotService spotService;

  /**
   * スポット一覧（カテゴリ・設備情報を JOIN 済）のデータを取得するエンドポイントです。
   * 検索条件は指定せず、論理削除されていないスポットを全件取得します。
   *
   * @return スポット一覧（SpotListItemDto のリスト）
   */
  @Operation(
      summary = "スポット一覧の取得【条件指定なし】",
      description = "カテゴリ情報・設備情報を JOIN 済みのスポット一覧を取得します。"
          + "検索条件は指定せず、論理削除されていないスポットを ID 昇順で返します。",
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
  public List<SpotListItemDto> getSpots() {
    return spotService.getSpotList();
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
