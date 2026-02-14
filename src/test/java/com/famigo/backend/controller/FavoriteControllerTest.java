package com.famigo.backend.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.famigo.backend.mapper.UserMapper;
import com.famigo.backend.security.AppUserPrincipal;
import com.famigo.backend.security.JwtTokenProvider;
import com.famigo.backend.service.FavoriteService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = FavoriteController.class)
class FavoriteControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private FavoriteService favoriteService;

  @MockitoBean
  private JwtTokenProvider jwtTokenProvider;

  @MockitoBean
  private UserMapper userMapper;

  private UsernamePasswordAuthenticationToken buildAuthWithAppUserPrincipal(Long userId) {

    AppUserPrincipal principal = mock(AppUserPrincipal.class);
    when(principal.getUserId()).thenReturn(userId);

    return new UsernamePasswordAuthenticationToken(
        principal,
        null,
        List.of(new SimpleGrantedAuthority("ROLE_USER"))
    );
  }

  @Test
  void お気に入り一覧取得_200で返ること() throws Exception {

    when(favoriteService.getFavorites(1L)).thenReturn(List.of());

    mockMvc.perform(
            get("/api/favorites")
                .with(authentication(buildAuthWithAppUserPrincipal(1L)))
        )
        .andExpect(status().isOk());
  }

  @Test
  void お気に入り登録_204で返ること() throws Exception {

    doNothing().when(favoriteService).addFavorite(1L, 1L);

    mockMvc.perform(
            post("/api/favorites/1")
                .with(authentication(buildAuthWithAppUserPrincipal(1L)))
                .with(csrf()) // CSRF対策
        )
        .andExpect(status().isNoContent());
  }

  @Test
  void お気に入り解除_204で返ること() throws Exception {

    doNothing().when(favoriteService).removeFavorite(1L, 1L);

    mockMvc.perform(
            delete("/api/favorites/1")
                .with(authentication(buildAuthWithAppUserPrincipal(1L)))
                .with(csrf()) // CSRF対策
        )
        .andExpect(status().isNoContent());
  }
}
