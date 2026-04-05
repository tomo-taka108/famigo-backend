package com.famigo.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security の設定クラス。
 * JWT（Bearer）前提で stateless にする。
 */
@Configuration // 設定クラスであることを示す
@EnableWebSecurity // Spring Security のウェブセキュリティ機能を有効化
@RequiredArgsConstructor // final フィールドのコンストラクタを自動生成（Lombok）
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtFilter;
  private final RestAuthenticationEntryPoint authenticationEntryPoint;
  private final RestAccessDeniedHandler accessDeniedHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        .csrf(csrf -> csrf.disable()) // CSRF対策を無効化（APIサーバー＆JWTなので不要なことが多い）
        .cors(Customizer.withDefaults()) // CORSを有効化（フロントエンドからのクロスドメイン通信を許可）
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // セッションを作らない（ステートレス）
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(authenticationEntryPoint) // 未ログイン時の401エラー応答
            .accessDeniedHandler(accessDeniedHandler) // 権限不足時の403エラー応答
        )

        // 匿名ユーザー＝GUEST（ログインしてない人にもROLEを与える）
        .anonymous(anon -> anon.authorities("ROLE_GUEST"))
        .authorizeHttpRequests(auth -> auth

            // CORS の Preflight
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            // 死活監視（ALB のヘルスチェック用）
            .requestMatchers(HttpMethod.GET, "/health").permitAll()

            // Swagger（開発用）
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html"
            ).permitAll()

            // 認証系
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/token").permitAll()

            // GUEST（未ログイン）で許可するAPI
            .requestMatchers(HttpMethod.GET, "/api/spots").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/spots/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/spots/*/reviews").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()

            // USER（ログイン）で許可するAPI（ADMIN も包含）
            .requestMatchers(HttpMethod.GET, "/api/users/me").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.GET, "/api/favorites").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/spots/*/reviews").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/spots/*/reviews/*").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/spots/*/reviews/*").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/favorites/*").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/favorites/*").hasAnyRole("USER", "ADMIN")

            // ユーザー自己管理（GET/PUT/DELETE は /api/users/me に集約）
            .requestMatchers(HttpMethod.PUT, "/api/users/me").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/users/me/password").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/users/me").hasAnyRole("USER", "ADMIN")

            // その他はログイン必須
            .anyRequest().hasAnyRole("USER", "ADMIN")
        )
        // ユーザー名パスワード認証フィルターの前に、自作の JWT フィルターを通す（JWTフィルターをどこで動かすかの設定）
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
