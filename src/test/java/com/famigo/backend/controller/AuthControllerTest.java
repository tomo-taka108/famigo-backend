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
import com.famigo.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class) // errorCode形式を返すハンドラも含めて確認できる
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private AuthService authService;

  @Test
  void ログイン_正常系_200でトークンが返ること() throws Exception {
    when(authService.login(any()))
        .thenReturn(new LoginResponse("dummy-token", "Bearer", 3600L, null));

    String json = """
        { "email": "demo@example.com", "password": "demo1234" }
        """;

    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("dummy-token"))
        .andExpect(jsonPath("$.tokenType").value("Bearer"));

    verify(authService, times(1)).login(any());
  }

  @Test
  void ログイン_バリデーションエラー_400になること() throws Exception {
    String json = """
        { "email": "", "password": "" }
        """;

    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));

    // バリデーションで弾かれるのでServiceは呼ばれない
    verify(authService, never()).login(any());
  }

  @Test
  void 登録_バリデーションエラー_400になること() throws Exception {
    String json = """
        { "displayName": "", "email": "invalid", "password": "", "passwordConfirm": "" }
        """;

    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));

    verify(authService, never()).register(any());
  }
}
