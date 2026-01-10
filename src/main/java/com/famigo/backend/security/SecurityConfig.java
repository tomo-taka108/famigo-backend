package com.famigo.backend.security;

import com.famigo.backend.mapper.UserMapper;
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
 * Spring Security の設定クラス。
 * JWT認証を前提とした「ステートレス（セッションなし）」構成にする。
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtTokenProvider jwtTokenProvider;

  private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
  private final RestAccessDeniedHandler restAccessDeniedHandler;

  private final UserMapper userMapper;


  /**
   * Securityのフィルタチェーンを構築する。
   *
   * @param http HttpSecurity
   * @return SecurityFilterChain
   * @throws Exception 設定構築例外
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtTokenProvider, userMapper);

    http
        .csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(restAuthenticationEntryPoint)
            .accessDeniedHandler(restAccessDeniedHandler)
        )
        // 匿名ユーザー＝GUEST（ログインしてない人にもROLEを与える）
        .anonymous(anon -> anon.authorities("ROLE_GUEST"))
        .authorizeHttpRequests(auth -> auth

            // --------------------------------------------
            // Swagger（開発用）
            // --------------------------------------------
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html"
            ).permitAll()

            // --------------------------------------------
            // 認証系
            // --------------------------------------------
            .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()

            // --------------------------------------------
            // GUEST（未ログイン）で許可するAPI（仕様固定）
            // --------------------------------------------
            .requestMatchers(HttpMethod.GET, "/spots").permitAll()
            .requestMatchers(HttpMethod.GET, "/spots/*").permitAll()
            .requestMatchers(HttpMethod.GET, "/spots/*/reviews").permitAll()

            // ※現状フロントの絞り込みで categories を使うため公開（必要なら後で仕様に明記して固定）
            .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()

            // --------------------------------------------
            // USER（ログイン）で許可するAPI（仕様固定）
            //   ADMIN も USER を包含するため hasAnyRole(USER, ADMIN)
            // --------------------------------------------
            .requestMatchers(HttpMethod.GET, "/auth/me").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.GET, "/favorites").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.POST, "/spots/*/reviews").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.PUT, "/spots/*/reviews/*").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/spots/*/reviews/*").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.POST, "/spots/*/favorites").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/spots/*/favorites").hasAnyRole("USER", "ADMIN")

            // ユーザー自己管理
            .requestMatchers(HttpMethod.PUT, "/users/me/display-name").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.PUT, "/users/me/email").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.PUT, "/users/me/password").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/users/me").hasAnyRole("USER", "ADMIN")

            // --------------------------------------------
            // ADMIN（将来の管理系API用）
            // --------------------------------------------
            .requestMatchers("/admin/**").hasRole("ADMIN")

            // --------------------------------------------
            // その他はログイン必須（GUESTは不可）
            // --------------------------------------------
            .anyRequest().hasAnyRole("USER", "ADMIN")
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }


  /**
   * パスワードハッシュ化用。 ログイン（ID/Password）を実装する際に、DBには平文を保存せずハッシュ（BCrypt）を保存する前提。
   *
   * @return PasswordEncoder（BCrypt）
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
