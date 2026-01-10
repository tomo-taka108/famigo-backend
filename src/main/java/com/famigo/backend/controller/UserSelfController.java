package com.famigo.backend.controller;

import com.famigo.backend.dto.auth.MeResponse;
import com.famigo.backend.dto.user.UpdateUserMeRequest;
import com.famigo.backend.security.AppUserPrincipal;
import com.famigo.backend.service.UserSelfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 「ログイン中ユーザーの自己管理（表示名/メールアドレス/パスワード変更、退会）」を提供する REST API の Controller クラスです。
 * マイページ（アカウント設定）から呼び出されます。
 */
@RestController
@RequestMapping("/users/me")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class UserSelfController {

  private final UserSelfService userSelfService;


  /**
   * 自分の表示名を変更するエンドポイント。
   *
   * @param request   変更内容（displayName）
   * @param principal ログイン中ユーザー情報（JWTから復元）
   * @return 更新後のログイン中ユーザー情報
   */
  @Operation(
      summary = "自分の表示名を変更（Update）",
      description = "ログイン中ユーザーの表示名（users.name）を更新します。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "更新成功",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = MeResponse.class)
              )
          )
      }
  )
  @PutMapping("/display-name")
  public MeResponse updateDisplayName(
      @Validated(UpdateUserMeRequest.DisplayNameUpdate.class)
      @RequestBody UpdateUserMeRequest request,
      @AuthenticationPrincipal AppUserPrincipal principal
  ) {
    return userSelfService.updateDisplayName(principal.getUserId(), request);
  }


  /**
   * 自分のメールアドレスを変更するエンドポイント。
   *
   * @param request   変更内容（email）
   * @param principal ログイン中ユーザー情報（JWTから復元）
   * @return 更新後のログイン中ユーザー情報
   */
  @Operation(
      summary = "自分のメールアドレスを変更（Update）",
      description = "ログイン中ユーザーのメールアドレス（users.email）を更新します。"
          + " email再利用は今回は対応しないため、退会ユーザーのemailも再利用不可です（DBのUNIQUE制約に従う）。",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "更新成功",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = MeResponse.class)
              )
          )
      }
  )
  @PutMapping("/email")
  public MeResponse updateEmail(
      @Validated(UpdateUserMeRequest.EmailUpdate.class)
      @RequestBody UpdateUserMeRequest request,
      @AuthenticationPrincipal AppUserPrincipal principal
  ) {
    return userSelfService.updateEmail(principal.getUserId(), request);
  }


  /**
   * 自分のパスワードを変更するエンドポイント。
   *
   * @param request   変更内容（currentPassword/newPassword/newPasswordConfirm）
   * @param principal ログイン中ユーザー情報（JWTから復元）
   */
  @Operation(
      summary = "自分のパスワードを変更（Update）",
      description = "ログイン中ユーザーのパスワード（users.password_hash）を更新します。",
      responses = {
          @ApiResponse(
              responseCode = "204",
              description = "更新成功（No Content）"
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


  /**
   * 退会（論理削除）するエンドポイント。
   *
   * @param principal ログイン中ユーザー情報（JWTから復元）
   */
  @Operation(
      summary = "退会（論理削除）（Delete）",
      description = "ログイン中ユーザーを退会（users.is_deleted=1）にし、表示名を「退会ユーザー」に更新します。レビューは残ります。",
      responses = {
          @ApiResponse(
              responseCode = "204",
              description = "退会成功（No Content）"
          )
      }
  )
  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void withdraw(@AuthenticationPrincipal AppUserPrincipal principal) {
    userSelfService.withdraw(principal.getUserId());
  }
}
