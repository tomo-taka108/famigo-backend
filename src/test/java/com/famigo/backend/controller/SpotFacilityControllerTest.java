package com.famigo.backend.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.famigo.backend.entity.SpotFacility;
import com.famigo.backend.exception.GlobalExceptionHandler;
import com.famigo.backend.service.SpotFacilityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 設備情報取得のController単体テスト。
 * entity返却でも「HTTP 200で返ること」を最低限押さえる。
 */
@WebMvcTest(SpotFacilityController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class SpotFacilityControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private SpotFacilityService spotFacilityService;

  @Test
  void 設備情報取得_200で返ること() throws Exception {
    SpotFacility facility = new SpotFacility();
    facility.setSpotId(1L);

    when(spotFacilityService.getSpotFacilityBySpotId(1L)).thenReturn(facility);

    mockMvc.perform(get("/spot-facilities/1"))
        .andExpect(status().isOk());

    verify(spotFacilityService, times(1)).getSpotFacilityBySpotId(eq(1L));
  }
}
