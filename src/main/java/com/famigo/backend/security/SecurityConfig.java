package com.famigo.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security の全体設定。
 * ポイント：
 *   セッションを使わない（ステートレス）
 *   JWTフィルタをログイン処理の前に挿入
 *   未認証=401 / 権限不足=403 をJSONで統一
 *   GETの一部は公開、それ以外は USER/ADMIN のみ許可
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  // JWT操作
  private final JwtTokenProvider jwtTokenProvider;

  // 未認証時 401 を返す
  private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

  // 権限不足時 403 を返す
  private final RestAccessDeniedHandler restAccessDeniedHandler;

  /**
   * Securityのフィルタチェーンを構築する。
   *
   * @param http HttpSecurity
   * @return SecurityFilterChain
   * @throws Exception 設定構築例外
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtTokenProvider);

    http
        .csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(restAuthenticationEntryPoint)
            .accessDeniedHandler(restAccessDeniedHandler)
        )
        // 匿名ユーザー＝GUEST（ログインしてない人にもROLEを与える）
        .anonymous(anon -> anon.authorities("ROLE_GUEST"))
        .authorizeHttpRequests(auth -> auth

            // 認証系（ログイン・登録など）
            .requestMatchers("/auth/**").permitAll()

            // Swagger
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html"
            ).permitAll()

            // 公開（未ログイン閲覧OK）
            .requestMatchers(HttpMethod.GET, "/spots/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/spots/*/reviews").permitAll()

            // それ以外はログイン必須
            .anyRequest().hasAnyRole("USER", "ADMIN")
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /**
   * パスワードハッシュ化用。
   * ログイン（ID/Password）を実装する際に、DBには平文を保存せずハッシュ（BCrypt）を保存する前提。
   *
   * @return PasswordEncoder（BCrypt）
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
