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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.famigo.backend.dto.auth.MeResponse;
import com.famigo.backend.exception.GlobalExceptionHandler;
import com.famigo.backend.security.AppUserPrincipal;
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
 * users/me系は「入力バリデーション」「Service呼び出し」が主に確認したいポイント。
 * JWT認証自体はここでは見ない（縦ラインテストで確認するのが良い）。
 */
@WebMvcTest(UserSelfController.class)
@AutoConfigureMockMvc // Favorite と同じ：addFilters=true（デフォルト）
@Import(GlobalExceptionHandler.class)
class UserSelfControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserSelfService userSelfService;

  private UsernamePasswordAuthenticationToken loginUser() {
    AppUserPrincipal principal = new AppUserPrincipal(
        1L,
        "test1@example.com",
        "USER",
        List.of(new SimpleGrantedAuthority("ROLE_USER"))
    );
    return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
  }

  @Test
  void 表示名およびメール更新_バリデーションエラーなら400になること() throws Exception {
    String json = """
      { "displayName": "", "email": "invalid" }
      """;

    mockMvc.perform(put("/users/me/profile")
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(json)
            .with(authentication(loginUser()))
            .with(csrf())) // 更新系(PUT)はCSRFトークン無しだと403になりControllerに到達しないため、テストでは付与する
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

    mockMvc.perform(put("/users/me/profile")
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(json)
            .with(authentication(loginUser()))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").value("new-name"))
        .andExpect(jsonPath("$.email").value("new@example.com"));

    verify(userSelfService, times(1)).updateProfile(eq(1L), any());
  }

  @Test
  void パスワード変更_正常系なら204になること() throws Exception {
    String json = """
      { "currentPassword": "currentpass", "newPassword": "newpass1234", "newPasswordConfirm": "newpass1234" }
      """;

    mockMvc.perform(put("/users/me/password")
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(json)
            .with(authentication(loginUser()))
            .with(csrf())) // PUT は CSRF が必要
        .andExpect(status().isNoContent());

    verify(userSelfService, times(1)).changePassword(eq(1L), any());
  }

  @Test
  void 退会_204になること() throws Exception {
    mockMvc.perform(delete("/users/me")
            .with(authentication(loginUser()))
            .with(csrf())) // 更新系(DELETE)はCSRFトークン無しだと403になりControllerに到達しないため、テストでは付与する
        .andExpect(status().isNoContent());

    verify(userSelfService, times(1)).withdraw(eq(1L));
  }
}
