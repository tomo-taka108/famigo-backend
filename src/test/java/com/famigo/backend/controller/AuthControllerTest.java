package com.famigo.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.famigo.backend.dto.auth.LoginResponse;
import com.famigo.backend.exception.GlobalExceptionHandler;
import com.famigo.backend.mapper.UserMapper;
import com.famigo.backend.security.JwtTokenProvider;
import com.famigo.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * AuthController の入力バリデーション／レスポンスを確認するテスト。
 * 注意：
 * WebMvcTest では Spring Security のデフォルト設定が混ざると
 * POST が CSRF により 403 になる場合がある。
 * このテストは「セキュリティ挙動」ではなく「Controllerの入力チェック」を見たいので、
 * addFilters=false でセキュリティフィルタを無効化する。
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  // Controller が依存する Service をモック
  @MockitoBean
  private AuthService authService;

  // jwtAuthenticationFilter が生成される構成の場合に備えてモック（Context起動失敗の再発防止）
  @MockitoBean
  private JwtTokenProvider jwtTokenProvider;

  @MockitoBean
  private UserMapper userMapper;

  @Test
  void ログイン_バリデーションエラー_400になること() throws Exception {

    String json = """
        { "email": "", "password": "" }
        """;

    mockMvc.perform(post("/api/auth/token")
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));

    verify(authService, never()).login(any());
  }

  @Test
  void ログイン_正常系_200でトークンが返ること() throws Exception {

    when(authService.login(any()))
        .thenReturn(new LoginResponse("dummy-token", "Bearer", 3600L, null));

    String json = """
        { "email": "demo@example.com", "password": "demo1234" }
        """;

    mockMvc.perform(post("/api/auth/token")
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(json))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("dummy-token"))
        .andExpect(jsonPath("$.tokenType").value("Bearer"));

    verify(authService, times(1)).login(any());
  }

  @Test
  void 登録_バリデーションエラー_400になること() throws Exception {

    String json = """
        { "displayName": "", "email": "invalid", "password": "", "passwordConfirm": "" }
        """;

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));

    verify(authService, never()).register(any());
  }
}
