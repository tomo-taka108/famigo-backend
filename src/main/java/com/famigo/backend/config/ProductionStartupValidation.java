package com.famigo.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * 本番環境（prod）での「設定漏れ」を起動時に検知して即座に落とすためのチェック。
 * 本番で JWT_SECRET 未設定のまま起動するとセキュリティ事故につながるため、起動そのものを失敗させる。
 * また、CORS 設定漏れは「フロントからAPIが叩けない」典型事故になるため、こちらも同様に検知する。
 */
@Component
@Profile("prod")
public class ProductionStartupValidation implements ApplicationRunner {

  @Value("${famigo.jwt.secret:}")
  private String jwtSecret;

  @Value("${famigo.cors.allowed-origins:}")
  private String corsAllowedOrigins;

  @Override
  public void run(ApplicationArguments args) {
    // ===============================
    // JWT
    // ===============================
    if (jwtSecret == null || jwtSecret.isBlank()) {
      throw new IllegalStateException("[prod] FAMIGO_JWT_SECRET が未設定です。安全のため起動を中断します。");
    }

    // うっかりデフォルト値（テンプレ）で運用する事故を防ぐ
    if (jwtSecret.contains("CHANGE_ME_TO_A_LONG_RANDOM_SECRET")) {
      throw new IllegalStateException(
          "[prod] JWT_SECRET がテンプレ値のままです。安全のため起動を中断します。");
    }

    // HS256は32文字以上が推奨（文字数ベースで雑にチェック）
    if (jwtSecret.length() < 32) {
      throw new IllegalStateException(
          "[prod] JWT_SECRET が短すぎます（32文字以上推奨）。安全のため起動を中断します。");
    }

    // ===============================
    // CORS
    // ===============================
    if (corsAllowedOrigins == null || corsAllowedOrigins.isBlank()) {
      throw new IllegalStateException(
          "[prod] FAMIGO_CORS_ALLOWED_ORIGINS が未設定です。フロント公開のため必須です。起動を中断します。");
    }
  }
}
