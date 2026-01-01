package com.famigo.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "ユーザー（usersテーブル）のエンティティ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Schema(description = "ユーザーID", example = "1")
  private Long id;

  @Schema(description = "ユーザー名（ニックネーム可）", example = "ゆうパパ")
  private String name;

  @Schema(description = "メールアドレス（ログインID）", example = "test@example.com")
  private String email;

  @Schema(description = "パスワードハッシュ（BCrypt）")
  private String passwordHash;

  @Schema(description = "ユーザーロール（GUEST / USER / ADMIN）", example = "USER")
  private String role;

  @Schema(description = "作成日時（DATETIME）", example = "2025-12-01T12:00:00")
  private LocalDateTime createdAt;

  @Schema(description = "更新日時（DATETIME）", example = "2025-12-01T12:00:00")
  private LocalDateTime updatedAt;

  @Schema(description = "論理削除フラグ", example = "false")
  private Boolean isDeleted;
}
