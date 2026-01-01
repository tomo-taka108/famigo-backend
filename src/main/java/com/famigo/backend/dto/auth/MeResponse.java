package com.famigo.backend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "ログイン中ユーザー情報DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeResponse {

  @Schema(description = "ユーザーID", example = "1")
  private Long id;

  @Schema(description = "ユーザー名（ニックネーム可）", example = "デモユーザー")
  private String name;

  @Schema(description = "メールアドレス（ログインID）", example = "demo@example.com")
  private String email;

  @Schema(description = "ユーザーロール（GUEST / USER / ADMIN）", example = "USER")
  private String role;
}
