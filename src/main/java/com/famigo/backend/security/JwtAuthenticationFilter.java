package com.famigo.backend.security;

import com.famigo.backend.entity.User;
import com.famigo.backend.mapper.UserMapper;
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
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Authorization（認可）ヘッダの Bearer JWT を読み取り、SecurityContext に Authentication をセットするフィルタ。
 * - JWTが有効でも、ユーザーが退会（論理削除）済みなら未ログイン扱いにする（DBで active user を確認）
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserMapper userMapper;

  public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserMapper userMapper) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.userMapper = userMapper;
  }

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

        // 退会済みユーザーは弾く（JWTが有効でも、DB上でactiveでなければ未ログイン扱い）
        User user = userMapper.findActiveById(userId);
        if (user == null) {
          SecurityContextHolder.clearContext();
          filterChain.doFilter(request, response);
          return;
        }

        String role = user.getRole();

        List<SimpleGrantedAuthority> authorities =
            List.of(new SimpleGrantedAuthority("ROLE_" + role));

        // email/role はDBから復元（メール変更後も principal の中身が最新になる）
        AppUserPrincipal principal =
            new AppUserPrincipal(user.getId(), user.getEmail(), role, authorities);

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
