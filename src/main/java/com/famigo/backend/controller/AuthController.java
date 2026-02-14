package com.famigo.backend.controller;

import com.famigo.backend.dto.auth.LoginRequest;
import com.famigo.backend.dto.auth.LoginResponse;
import com.famigo.backend.dto.auth.RegisterRequest;
import com.famigo.backend.exception.ErrorResponse;
import com.famigo.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 外部認証（ユーザー登録・ログイン）を提供する REST API の Controller クラスです。
 * ポイント：
 *   登録（POST /api/users）とログイン（POST /api/auth/token）をこのControllerでまとめて扱う。
 *   登録成功時も JWT を発行して返却（= 登録後に自動ログイン）する。</li>
 * ※ /api/users は「ユーザー作成」だが、実装上は登録後にJWT発行まで行うため「外部認証フロー」の一部として扱う。
 */
@Tag(name = "外部認証", description = "ユーザー登録 / ログイン（JWT発行）")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  /**
   * ユーザー登録（サインアップ）を行うエンドポイント。
   * 登録成功時は JWT を発行して返却します（= 登録後に自動ログインする想定）。
   *
   * @param request ユーザー登録要求
   * @return 登録結果（JWTなど：LoginResponse）
   */
  @Operation(
      summary = "ユーザー登録（サインアップ）",
      description = "displayName / email / password / passwordConfirm を受け取り、ユーザーを作成します。登録成功時はJWTを発行して返却します。",
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "登録成功（JWT発行）",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = LoginResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "入力不正（バリデーションエラー）",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "409",
              description = "登録失敗（email重複）",
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
  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  public LoginResponse register(@RequestBody @Valid RegisterRequest request) {
    return authService.register(request);
  }


  /**
   * ログイン（JWT発行）を行うエンドポイント。
   * email / password が正しい場合に JWT を発行し、フロントエンドへ返却します。
   *
   * @param request ログイン要求（email / password）
   * @return ログイン結果（JWTなど：LoginResponse）
   */
  @Operation(
      summary = "ログイン（JWT発行）",
      description = "email / password が正しい場合に JWT を発行します。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "ログイン成功",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = LoginResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "入力不正（バリデーションエラー）",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "401",
              description = "認証失敗（email/password不一致）",
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
  @PostMapping("/auth/token")
  public LoginResponse token(@RequestBody @Valid LoginRequest request) {
    return authService.login(request);
  }
}
