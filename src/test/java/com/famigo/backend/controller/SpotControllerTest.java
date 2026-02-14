package com.famigo.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.famigo.backend.dto.SpotDetailDto;
import com.famigo.backend.dto.SpotListItemDto;
import com.famigo.backend.exception.GlobalExceptionHandler;
import com.famigo.backend.mapper.UserMapper;
import com.famigo.backend.security.JwtTokenProvider;
import com.famigo.backend.service.SpotService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SpotController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class SpotControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private SpotService spotService;

  // 保険
  @MockitoBean
  private JwtTokenProvider jwtTokenProvider;

  @MockitoBean
  private UserMapper userMapper;

  @Test
  void スポット一覧_200で返ること() throws Exception {
    when(spotService.getSpotList(any(), any())).thenReturn(List.of(new SpotListItemDto()));

    mockMvc.perform(get("/api/spots"))
        .andExpect(status().isOk());

    verify(spotService, times(1)).getSpotList(any(), any());
  }

  @Test
  void スポット詳細_200で返ること() throws Exception {
    SpotDetailDto dto = new SpotDetailDto();
    dto.setId(1L);
    dto.setName("テストスポット");

    when(spotService.getSpotDetail(eq(1L), any())).thenReturn(dto);

    mockMvc.perform(get("/api/spots/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("テストスポット"));

    verify(spotService, times(1)).getSpotDetail(eq(1L), any());
  }
}
