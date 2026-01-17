package com.famigo.backend.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.famigo.backend.dto.SpotListItemDto;
import com.famigo.backend.exception.GlobalExceptionHandler;
import com.famigo.backend.security.AppUserPrincipal;
import com.famigo.backend.service.FavoriteService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FavoriteController.class)
@AutoConfigureMockMvc // addFilters=true（デフォルト）でOK
@Import(GlobalExceptionHandler.class)
class FavoriteControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private FavoriteService favoriteService;

  private UsernamePasswordAuthenticationToken loginUser() {
    AppUserPrincipal principal = new AppUserPrincipal(1L, "test1@example.com", "USER", List.of());
    return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
  }

  @Test
  void お気に入り一覧取得_200で返ること() throws Exception {
    when(favoriteService.getFavorites(1L)).thenReturn(List.of(new SpotListItemDto()));

    mockMvc.perform(get("/favorites")
            .with(authentication(loginUser())))
        .andExpect(status().isOk());

    verify(favoriteService, times(1)).getFavorites(eq(1L));
  }

  @Test
  void お気に入り登録_200で返ること() throws Exception {
    mockMvc.perform(post("/spots/1/favorites")
            .with(authentication(loginUser()))
            .with(csrf())) // ← これが必要（POSTはCSRF必須）
        .andExpect(status().isOk());

    verify(favoriteService, times(1)).addFavorite(eq(1L), eq(1L));
  }

  @Test
  void お気に入り解除_200で返ること() throws Exception {
    mockMvc.perform(delete("/spots/1/favorites")
            .with(authentication(loginUser()))
            .with(csrf())) // ← これが必要（DELETEもCSRF必須）
        .andExpect(status().isOk());

    verify(favoriteService, times(1)).removeFavorite(eq(1L), eq(1L));
  }
}
