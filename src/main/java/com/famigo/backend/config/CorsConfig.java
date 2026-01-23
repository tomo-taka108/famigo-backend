package com.famigo.backend.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * CORS（Cross-Origin Resource Sharing）のグローバル設定。
 * ローカル開発では http://localhost:5173（Vite）からのアクセスを許可し、
 * 本番では https://famigo.jp などの正規ドメインのみ許可する。
 * Controllerごとの @CrossOrigin は「本番のドメイン差し替え漏れ」事故を起こしやすいため、
 * このクラスで一元管理する方針とする。
 */
@Configuration
public class CorsConfig {

  /**
   * 許可するOrigin（カンマ区切り）。
   * 例：
   *   ローカル：http://localhost:5173
   *   本番：https://famigo.jp
   */
  @Value("${famigo.cors.allowed-origins}")
  private String allowedOrigins;

  /**
   * Spring Security の .cors() と連携する CORS 設定。
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    // カンマ区切り文字列をListへ変換（空白は除去）
    List<String> origins = Arrays.stream(allowedOrigins.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toList());

    config.setAllowedOrigins(origins);

    // SPAからのAPI呼び出しで使うメソッドを許可
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

    // Authorization(JWT) を含むヘッダを許可
    // （実際のブラウザは Accept なども付与するため、ここはワイルドカードで許可しておく）
    config.setAllowedHeaders(List.of("*"));

    // Cookieを使わない（Bearerトークン方式）想定のため false
    config.setAllowCredentials(false);

    // Preflightのキャッシュ（秒）
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
