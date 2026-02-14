package com.famigo.backend.controller;

import com.famigo.backend.dto.auth.MeResponse;
import com.famigo.backend.dto.user.UpdateUserMeRequest;
import com.famigo.backend.exception.ErrorResponse;
import com.famigo.backend.security.AppUserPrincipal;
import com.famigo.backend.service.UserSelfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * ユーザー情報の管理（ログイン中ユーザーの自己管理）を提供する REST API の Controller クラスです。
 * 提供機能：
 *   自分のユーザー情報取得（GET /api/users/me）
 *   プロフィール更新（PUT /api/users/me）
 *   パスワード更新（PUT /api/users/me/password）
 *   退会（DELETE /api/users/me）
 */
@Tag(name = "ユーザー情報管理", description = "ログイン中ユーザーの取得 / 更新 / 退会")
@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserSelfController {

  private final UserSelfService userSelfService;


  @Operation(
      summary = "自分のユーザー情報を取得（Read）",
      description = "ログイン中ユーザーの基本情報（id / name / email / role）を返します。",
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
              description = "未ログイン / トークン不正",
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
  public MeResponse me(@AuthenticationPrincipal AppUserPrincipal principal) {
    return userSelfService.getMe(principal.getUserId());
  }


  @Operation(
      summary = "自分のプロフィールを変更（Update）（表示名+メール）",
      description = "ログイン中ユーザーの表示名（users.name）とメールアドレス（users.email）を同時に更新します。"
          + " 方針（原子性）：入力エラーがある場合は、どの項目も更新しません。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "更新成功",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = MeResponse.class)
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
              description = "未ログイン / トークン不正",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "403",
              description = "権限不足（GUEST等）",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "409",
              description = "更新失敗（email重複）",
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
  @PutMapping
  public MeResponse updateProfile(
      @Validated(UpdateUserMeRequest.ProfileUpdate.class)
      @RequestBody UpdateUserMeRequest request,
      @AuthenticationPrincipal AppUserPrincipal principal
  ) {
    return userSelfService.updateProfile(principal.getUserId(), request);
  }


  @Operation(
      summary = "自分のパスワードを変更（Update）",
      description = "ログイン中ユーザーのパスワード（users.password_hash）を更新します。",
      responses = {
          @ApiResponse(responseCode = "204", description = "更新成功（No Content）", content = @Content),
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
              description = "未ログイン / トークン不正",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "403",
              description = "権限不足（GUEST等）",
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
  @PutMapping("/password")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void changePassword(
      @Validated(UpdateUserMeRequest.PasswordChange.class)
      @RequestBody UpdateUserMeRequest request,
      @AuthenticationPrincipal AppUserPrincipal principal
  ) {
    userSelfService.changePassword(principal.getUserId(), request);
  }


  @Operation(
      summary = "退会（論理削除）（Delete）",
      description = "ログイン中ユーザーを退会（users.is_deleted=1）にし、表示名を「退会ユーザー」に更新します。レビューは残ります。",
      responses = {
          @ApiResponse(responseCode = "204", description = "退会成功（No Content）", content = @Content),
          @ApiResponse(
              responseCode = "401",
              description = "未ログイン / トークン不正",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "403",
              description = "権限不足（GUEST等）",
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
  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void withdraw(@AuthenticationPrincipal AppUserPrincipal principal) {
    userSelfService.withdraw(principal.getUserId());
  }
}
