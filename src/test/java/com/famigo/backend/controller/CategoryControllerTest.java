package com.famigo.backend.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.famigo.backend.dto.CategoryDto;
import com.famigo.backend.exception.GlobalExceptionHandler;
import com.famigo.backend.mapper.UserMapper;
import com.famigo.backend.security.JwtTokenProvider;
import com.famigo.backend.service.CategoryService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CategoryControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private CategoryService categoryService;

  // Securityフィルタが生成される構成でも Context が落ちないように保険でモック
  @MockitoBean
  private JwtTokenProvider jwtTokenProvider;

  @MockitoBean
  private UserMapper userMapper;

  @Test
  void カテゴリ一覧_200で返ること() throws Exception {
    when(categoryService.getAll()).thenReturn(List.of(new CategoryDto()));

    mockMvc.perform(get("/api/categories"))
        .andExpect(status().isOk());

    verify(categoryService, times(1)).getAll();
  }
}
