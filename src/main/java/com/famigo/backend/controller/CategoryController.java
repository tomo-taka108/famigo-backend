package com.famigo.backend.controller;

import com.famigo.backend.dto.CategoryDto;
import com.famigo.backend.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 「カテゴリ一覧」を提供する REST API の Controller クラスです。
 */
@RestController
@RequestMapping("/categories")   // このコントローラーで扱うURLの共通プレフィックスを設定
@CrossOrigin(origins = "http://localhost:5173")    // Viteのフロントからのアクセスを許可
@RequiredArgsConstructor    // finalフィールドを引数に持つコンストラクタを自動生成
public class CategoryController {

  private final CategoryService categoryService;

  /**
   * カテゴリ一覧を取得するエンドポイント。
   *
   * @return List<CategoryDto>（カテゴリ一覧）
   */
  @Operation(
      summary = "カテゴリ一覧の取得",
      description = "categories テーブルのカテゴリ一覧を ID 昇順で取得します。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "一覧取得成功",
              content = @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(
                      schema = @Schema(implementation = CategoryDto.class)
                  )
              )
          )
          // 将来的に 400 / 404 / 500 などをハンドリングする場合は、ここに ApiResponse を追加していく想定
      }
  )

  @GetMapping
  public List<CategoryDto> getCategories() {
    return categoryService.getAll();
  }

}
