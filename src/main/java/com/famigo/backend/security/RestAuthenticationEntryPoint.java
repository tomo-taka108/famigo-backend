package com.famigo.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 未認証（Authenticationが無い）状態で保護APIにアクセスされた場合の入口。
 * Spring Security が 401 を返すタイミングで呼ばれ、FamigoではJSONで統一したレスポンスを返す。
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

  /**
   * 未認証時のレスポンス生成。
   *
   * @param request リクエスト
   * @param response レスポンス
   * @param authException 例外（未認証理由）
   */
  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException
  ) throws IOException {

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write("{\"message\":\"Unauthorized\"}");
  }
}
