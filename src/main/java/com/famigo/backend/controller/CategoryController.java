package com.famigo.backend.controller;

import com.famigo.backend.dto.CategoryDto;
import com.famigo.backend.exception.ErrorResponse;
import com.famigo.backend.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 「カテゴリ一覧」を提供する REST API の Controller クラスです。
 */
@RestController
@Tag(name = "カテゴリ", description = "カテゴリ一覧")
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;


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
  public List<CategoryDto> getCategories() {
    return categoryService.getAll();
  }
}
