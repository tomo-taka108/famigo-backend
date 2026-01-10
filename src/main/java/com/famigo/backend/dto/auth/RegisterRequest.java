package com.famigo.backend.dto.auth;

import com.famigo.backend.validation.ValidationGroups.First;
import com.famigo.backend.validation.ValidationGroups.Second;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ユーザー登録（サインアップ）要求DTO
 * /auth/register で使用します。
 * 【ポイント】
 * - 空欄のときは NotBlank だけを出す（Size の余計なエラーを出さない）
 *   → GroupSequence + groups を利用
 * - パスワード確認（passwordConfirm）と一致しない場合は AssertTrue で検知
 */
@Schema(description = "ユーザー登録（サインアップ）要求")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@GroupSequence({First.class, Second.class, RegisterRequest.class})
public class RegisterRequest {

  @Schema(description = "表示名（ニックネーム可）", example = "ゆうパパ")
  @NotBlank(message = "表示名を入力してください", groups = First.class)
  @Size(min = 3, max = 100, message = "表示名は3文字以上で入力してください", groups = Second.class)
  private String displayName;

  @Schema(description = "メールアドレス（ログインID）", example = "test@example.com")
  @NotBlank(message = "メールアドレスを入力してください", groups = First.class)
  @Email(message = "有効なメールアドレスを入力してください", groups = Second.class)
  @Size(max = 255, message = "メールアドレスは255文字以内で入力してください", groups = Second.class)
  private String email;

  @Schema(description = "パスワード（6文字以上）", example = "Passw0rd!")
  @NotBlank(message = "パスワードを入力してください", groups = First.class)
  @Size(min = 6, max = 72, message = "パスワードは6文字以上で入力してください", groups = Second.class)
  private String password;

  @Schema(description = "パスワード確認", example = "Passw0rd!")
  @NotBlank(message = "パスワード確認を入力してください", groups = First.class)
  private String passwordConfirm;

  @AssertTrue(message = "パスワードが一致しません") // メソッドがtrueを返すことを要求するアノテーション（falseの場合はエラーメッセージを表示）
  public boolean isPasswordMatched() {
    if (password == null || passwordConfirm == null) {
      return true; // 未入力エラーは NotBlank に任せたいからここではtrueを返す（責務の分離）
    }
    return password.equals(passwordConfirm);
  }

}

