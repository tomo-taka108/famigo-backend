package com.famigo.backend.controller;

import com.famigo.backend.dto.auth.LoginRequest;
import com.famigo.backend.dto.auth.LoginResponse;
import com.famigo.backend.dto.auth.MeResponse;
import com.famigo.backend.dto.auth.RegisterRequest;
import com.famigo.backend.security.AppUserPrincipal;
import com.famigo.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 「認証（ログイン）・ログイン中ユーザー情報」を提供する REST API の Controller クラスです。
 * フロントエンドのログイン画面や、ログイン状態チェック（/auth/me）から呼び出されます。
 */
@RestController
@RequestMapping("/auth")   // このコントローラーで扱うURLの共通プレフィックスを設定
@RequiredArgsConstructor    // finalフィールドを引数に持つコンストラクタを自動生成
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
              description = "入力不正（バリデーションエラー）"
          ),
          @ApiResponse(
              responseCode = "409",
              description = "登録失敗（email重複）"
          )
      }
  )
  @PostMapping("/register")
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
              responseCode = "401",
              description = "認証失敗（email/password不一致）"
          )
      }
  )
  @PostMapping("/login")
  public LoginResponse login(@RequestBody @Valid LoginRequest request) {
    return authService.login(request);
  }


  /**
   * ログイン中ユーザー情報（自分のユーザー情報）を取得するエンドポイント。
   * Authorization: Bearer {token} が必要です。
   *
   * @param principal 認証済みユーザー情報（JWTから生成された認証プリンシパル）
   * @return ログイン中ユーザー情報（MeResponse）
   */
  @Operation(
      summary = "ログイン中ユーザー情報を取得（JWT必須）",
      description = "Authorization: Bearer {token} が必要です。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "取得成功",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = MeResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "401",
              description = "未ログイン / トークン不正"
          )
      }
  )
  @GetMapping("/me")
  public MeResponse me(@AuthenticationPrincipal AppUserPrincipal principal) {
    return authService.me(principal.getUserId());
  }
}
