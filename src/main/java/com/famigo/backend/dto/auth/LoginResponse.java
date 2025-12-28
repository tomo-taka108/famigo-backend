package com.famigo.backend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "ログインレスポンスDTO（JWT発行結果）")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

  @Schema(description = "JWTアクセストークン", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
  private String accessToken;

  @Schema(description = "アクセストークンの種別", example = "Bearer")
  private String tokenType;

  @Schema(description = "アクセストークンの有効期限（秒）。発行時点からの秒数", example = "3600")
  private Long expiresIn;

  @Schema(description = "ログインユーザー情報（項目はMeResponseのDTOで別途定義）")
  private MeResponse user;
}
