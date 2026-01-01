package com.famigo.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * 認証はされているが、権限が足りない場合のハンドラ。
 * 例：ROLE_USER のみ許可のAPIに ROLE_GUEST でアクセスした場合など。
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

  /**
   * 権限不足時のレスポンス生成。
   *
   * @param request リクエスト
   * @param response レスポンス
   * @param accessDeniedException 例外（拒否理由）
   */
  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException
  ) throws IOException {

    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write("{\"message\":\"Forbidden\"}");
  }
}
