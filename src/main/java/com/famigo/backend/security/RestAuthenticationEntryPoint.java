package com.famigo.backend.security;

import com.famigo.backend.exception.ErrorCode;
import com.famigo.backend.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 未認証（Authenticationが無い）状態で保護APIにアクセスされた場合の入口。
 * Spring Security が 401 を返すタイミングで呼ばれ、FamigoではJSONで統一したレスポンスを返す。
 */
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

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

    ErrorResponse body = new ErrorResponse(
        ErrorCode.AUTH_REQUIRED.name(),
        "Authentication required: missing or invalid Bearer token."
    );

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write(objectMapper.writeValueAsString(body));
  }
}
