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
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtFilter;
  private final RestAuthenticationEntryPoint authenticationEntryPoint;
  private final RestAccessDeniedHandler accessDeniedHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        .csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
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
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
