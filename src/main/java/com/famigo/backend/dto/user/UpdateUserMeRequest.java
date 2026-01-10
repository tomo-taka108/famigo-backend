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
 * ユーザー自己情報更新用DTO（表示名変更 / メール変更 / パスワード変更）
 * 【ポイント】
 * - どの項目を必須にするかは Controller 側で @Validated(グループ) で切り替える
 * - 「空欄なのにSizeも出る」などの二重エラーを避けるため、グループシーケンスを使う
 */
@Schema(description = "ユーザー自己情報更新リクエスト（用途により必須項目が変わる）")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserMeRequest {

  // =========================================================
  // 表示名変更用：グループ（First/Secondを継承して「表示名専用」にする）
  // =========================================================
  public interface DisplayNameFirst extends First {}
  public interface DisplayNameSecond extends Second {}

  @GroupSequence({DisplayNameFirst.class, DisplayNameSecond.class})
  public interface DisplayNameUpdate {}


  // =========================================================
  // メールアドレス変更用：グループ
  // =========================================================
  public interface EmailFirst extends First {}
  public interface EmailSecond extends Second {}

  @GroupSequence({EmailFirst.class, EmailSecond.class})
  public interface EmailUpdate {}


  // =========================================================
  // パスワード変更用：グループ
  // =========================================================
  public interface PasswordFirst extends First {}
  public interface PasswordSecond extends Second {}

  @GroupSequence({PasswordFirst.class, PasswordSecond.class})
  public interface PasswordChange {}


  // =========================================================
  // 表示名（display-name）
  // =========================================================
  @Schema(description = "新しい表示名（ニックネーム可）", example = "ゆうパパ")
  @NotBlank(message = "表示名を入力してください", groups = DisplayNameFirst.class)
  @Size(min = 3, max = 100, message = "表示名は3文字以上で入力してください", groups = DisplayNameSecond.class)
  private String displayName;


  // =========================================================
  // メール（email）
  // =========================================================
  @Schema(description = "新しいメールアドレス（ログインID）", example = "newmail@example.com")
  @NotBlank(message = "メールアドレスを入力してください", groups = EmailFirst.class)
  @Email(message = "有効なメールアドレスを入力してください", groups = EmailSecond.class)
  @Size(max = 255, message = "メールアドレスは255文字以内で入力してください", groups = EmailSecond.class)
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
