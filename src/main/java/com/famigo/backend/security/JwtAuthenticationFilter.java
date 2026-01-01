package com.famigo.backend.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Authorization（認可） ヘッダの Bearer JWT を読み取り、SecurityContext に Authentication（認証） をセットするフィルタ。
 * このフィルタがセットした principal は {@link AppUserPrincipal} であり、Controller/Service から userId を取り出す土台になる。
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  // JWTの解析・検証を行う共通部品
  private final JwtTokenProvider jwtTokenProvider;

  public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }


  /**
   * 1リクエストにつき1回実行されるフィルタ本体。
   * Bearerトークンがあり、検証成功した場合のみ認証済み扱いにする。
   * 失敗した場合は SecurityContext を空にして後続へ渡す（=未ログイン扱い）。
   */
  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {

    String header = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {

      String token = header.substring(7);

      try {
        Claims claims = jwtTokenProvider.parseClaims(token);

        Long userId = ((Number) claims.get("uid")).longValue();
        String email = claims.getSubject();
        String role = (String) claims.get("role");

        List<SimpleGrantedAuthority> authorities =
            List.of(new SimpleGrantedAuthority("ROLE_" + role));

        AppUserPrincipal principal = new AppUserPrincipal(userId, email, role, authorities);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(principal, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

      } catch (Exception e) {
        // 署名不正・期限切れ・形式不正など → 未認証として扱う
        SecurityContextHolder.clearContext();
      }
    }

    filterChain.doFilter(request, response);
  }
}
