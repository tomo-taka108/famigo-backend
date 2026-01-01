package com.famigo.backend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "ログインリクエストDTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

  @Schema(description = "メールアドレス（ログインID）", example = "demo@example.com")
  @NotBlank
  @Email
  private String email;

  @Schema(description = "パスワード（平文）", example = "demo1234")
  @NotBlank
  private String password;
}
