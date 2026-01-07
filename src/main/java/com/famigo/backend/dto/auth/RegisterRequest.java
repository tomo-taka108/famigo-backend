package com.famigo.backend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ユーザー登録（サインアップ）要求DTO
 * /auth/register で使用します。
 */
@Schema(description = "ユーザー登録（サインアップ）要求")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

  @Schema(description = "表示名（アプリ内表示名）", example = "ゆうパパ")
  @NotBlank(message = "表示名を入力してください")
  @Size(min = 3, max = 100, message = "表示名は3文字以上で入力してください")
  private String displayName;

  @Schema(description = "メールアドレス（ログインID）", example = "test@example.com")
  @NotBlank(message = "メールアドレスを入力してください")
  @Email(message = "有効なメールアドレスを入力してください")
  @Size(max = 255, message = "メールアドレスは255文字以内で入力してください")
  private String email;

  @Schema(description = "パスワード（6文字以上）", example = "Passw0rd!")
  @NotBlank(message = "パスワードを入力してください")
  @Size(min = 6, max = 72, message = "パスワードは6文字以上で入力してください")
  private String password;

  @Schema(description = "パスワード確認", example = "Passw0rd!")
  @NotBlank(message = "パスワード確認を入力してください")
  private String passwordConfirm;

  @AssertTrue(message = "パスワードが一致しません") // メソッドがtrueを返すことを要求するアノテーション（falseの場合はエラーメッセージを表示）
  public boolean isPasswordMatched() {
    if (password == null || passwordConfirm == null) {
      return true; // 未入力エラーは NotBlank に任せたいからここではtrueを返す（責務の分離）
    }
    return password.equals(passwordConfirm);
  }

}

