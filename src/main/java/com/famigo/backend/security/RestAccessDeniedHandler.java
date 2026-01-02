package com.famigo.backend.security;

import com.famigo.backend.exception.ErrorCode;
import com.famigo.backend.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * 認証はされているが、権限が足りない場合のハンドラ。
 * 例：ROLE_USER のみ許可のAPIに ROLE_GUEST でアクセスした場合など。
 */
@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper;

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

    ErrorResponse body = new ErrorResponse(
        ErrorCode.ACCESS_DENIED.name(),
        "Access denied: insufficient permissions for this resource."
    );

    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json;charset=UTF-8");
    response.getWriter().write(objectMapper.writeValueAsString(body));
  }
}
