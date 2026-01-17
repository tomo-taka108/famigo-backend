package com.famigo.backend.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.famigo.backend.dto.ReviewListItemDto;
import com.famigo.backend.exception.GlobalExceptionHandler;
import com.famigo.backend.service.ReviewService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ReviewControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ReviewService reviewService;

  @Test
  void レビュー一覧取得_200で返ること() throws Exception {
    when(reviewService.getReviewsBySpotId(1L)).thenReturn(List.of(new ReviewListItemDto()));

    mockMvc.perform(get("/spots/1/reviews"))
        .andExpect(status().isOk());

    verify(reviewService, times(1)).getReviewsBySpotId(eq(1L));
  }
}
