package com.famigo.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.famigo.backend.dto.auth.MeResponse;
import com.famigo.backend.exception.GlobalExceptionHandler;
import com.famigo.backend.mapper.UserMapper;
import com.famigo.backend.security.AppUserPrincipal;
import com.famigo.backend.security.JwtTokenProvider;
import com.famigo.backend.service.UserSelfService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * UserSelfController は入力バリデーションと Service 呼び出し確認が目的。
 * 認可境界（401/403）は別途 Security テストに分離する。
 */
@WebMvcTest(UserSelfController.class)
@AutoConfigureMockMvc // ★ addFilters=false をやめる（フィルターONに戻す）
@Import(GlobalExceptionHandler.class)
class UserSelfControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserSelfService userSelfService;

  // JwtAuthenticationFilter が必要とする依存（コンテキスト起動エラー回避）
  @MockitoBean
  private JwtTokenProvider jwtTokenProvider;

  @MockitoBean
  private UserMapper userMapper;

  /**
   * @AuthenticationPrincipal AppUserPrincipal に渡す principal を作るヘルパー
   */
  private UsernamePasswordAuthenticationToken buildAuth(Long userId) {

    AppUserPrincipal principal = new AppUserPrincipal(
        userId,
        "me@example.com",
        "USER",
        List.of(new SimpleGrantedAuthority("ROLE_USER"))
    );

    return new UsernamePasswordAuthenticationToken(
        principal,
        null,
        principal.getAuthorities()
    );
  }

  @Test
  void 自分のユーザー情報取得_200で返ること() throws Exception {

    MeResponse res = new MeResponse(1L, "me-name", "me@example.com", "USER");
    when(userSelfService.getMe(1L)).thenReturn(res);

    mockMvc.perform(
            get("/api/users/me")
                .with(authentication(buildAuth(1L)))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("me-name"))
        .andExpect(jsonPath("$.email").value("me@example.com"))
        .andExpect(jsonPath("$.role").value("USER"));

    verify(userSelfService, times(1)).getMe(eq(1L));
  }

  @Test
  void プロフィール更新_バリデーションエラーなら400になること() throws Exception {

    String json = """
      { "displayName": "", "email": "invalid" }
      """;

    mockMvc.perform(
            put("/api/users/me")
                .with(authentication(buildAuth(1L)))
                .with(csrf()) // PUT は CSRF が必要（ないと 403 で Controller に到達しない）
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(json)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));

    verify(userSelfService, never()).updateProfile(any(), any());
  }

  @Test
  void プロフィール更新_200で返ること() throws Exception {

    MeResponse res = new MeResponse(1L, "new-name", "new@example.com", "USER");
    when(userSelfService.updateProfile(eq(1L), any())).thenReturn(res);

    String json = """
      { "displayName": "new-name", "email": "new@example.com" }
      """;

    mockMvc.perform(
            put("/api/users/me")
                .with(authentication(buildAuth(1L)))
                .with(csrf()) // PUT は CSRF 必須
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(json)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("new-name"))
        .andExpect(jsonPath("$.email").value("new@example.com"));

    verify(userSelfService, times(1)).updateProfile(eq(1L), any());
  }

  @Test
  void パスワード変更_正常系なら204になること() throws Exception {

    String json = """
      { "currentPassword": "currentpass", "newPassword": "newpass1234", "newPasswordConfirm": "newpass1234" }
      """;

    mockMvc.perform(
            put("/api/users/me/password")
                .with(authentication(buildAuth(1L)))
                .with(csrf()) // PUT は CSRF 必須
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(json)
        )
        .andExpect(status().isNoContent());

    verify(userSelfService, times(1)).changePassword(eq(1L), any());
  }

  @Test
  void 退会_204になること() throws Exception {

    mockMvc.perform(
            delete("/api/users/me")
                .with(authentication(buildAuth(1L)))
                .with(csrf()) // ★ DELETE は CSRF 必須
        )
        .andExpect(status().isNoContent());

    verify(userSelfService, times(1)).withdraw(eq(1L));
  }
}
