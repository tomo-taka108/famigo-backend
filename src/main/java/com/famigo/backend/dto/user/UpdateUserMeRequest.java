package com.famigo.backend.dto.user;

import com.famigo.backend.validation.ValidationGroups.First;
import com.famigo.backend.validation.ValidationGroups.Second;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ユーザー自己情報更新用DTO（プロフィール更新 / パスワード変更）
 * 【ポイント】
 * - 必須項目は Controller 側で @Validated(グループ) で切り替える
 * - 「空欄なのにSizeも出る」などの二重エラーを避けるため、グループシーケンスを使う
 */
@Schema(description = "ユーザー自己情報更新リクエスト（用途により必須項目が変わる）")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserMeRequest {

  // =========================================================
  // プロフィール更新（表示名 + メール）用：グループ
  // =========================================================
  public interface ProfileFirst extends First {}
  public interface ProfileSecond extends Second {}

  @GroupSequence({ProfileFirst.class, ProfileSecond.class})
  public interface ProfileUpdate {}

  // =========================================================
  // パスワード変更用：グループ
  // =========================================================
  public interface PasswordFirst extends First {}
  public interface PasswordSecond extends Second {}

  @GroupSequence({PasswordFirst.class, PasswordSecond.class})
  public interface PasswordChange {}

  // =========================================================
  // 表示名（displayName）
  // =========================================================
  @Schema(description = "新しい表示名（ニックネーム可）", example = "ゆうパパ")
  @NotBlank(message = "表示名（ユーザー名）を入力してください", groups = ProfileFirst.class)
  @Size(min = 3, max = 100, message = "表示名（ユーザー名）は3文字以上で入力してください", groups = ProfileSecond.class)
  private String displayName;

  // =========================================================
  // メール（email）
  // =========================================================
  @Schema(description = "新しいメールアドレス（ログインID）", example = "newmail@example.com")
  @NotBlank(message = "メールアドレスを入力してください", groups = ProfileFirst.class)
  @Email(message = "有効なメールアドレスを入力してください", groups = ProfileSecond.class)
  @Size(max = 255, message = "メールアドレスは255文字以内で入力してください", groups = ProfileSecond.class)
  private String email;

  // =========================================================
  // パスワード（password）
  // =========================================================
  @Schema(description = "現在のパスワード", example = "Passw0rd!")
  @NotBlank(message = "現在のパスワードを入力してください", groups = PasswordFirst.class)
  private String currentPassword;

  @Schema(description = "新しいパスワード（6文字以上）", example = "NewPassw0rd!")
  @NotBlank(message = "新しいパスワードを入力してください", groups = PasswordFirst.class)
  @Size(min = 6, max = 72, message = "新しいパスワードは6文字以上で入力してください", groups = PasswordSecond.class)
  private String newPassword;

  @Schema(description = "新しいパスワード確認", example = "NewPassw0rd!")
  @NotBlank(message = "新しいパスワード確認を入力してください", groups = PasswordFirst.class)
  private String newPasswordConfirm;

  @AssertTrue(message = "新しいパスワードが一致しません", groups = PasswordSecond.class)
  public boolean isNewPasswordMatched() {
    if (newPassword == null || newPasswordConfirm == null) {
      return true; // 未入力エラーは NotBlank に任せる
    }
    return newPassword.equals(newPasswordConfirm);
  }
}
