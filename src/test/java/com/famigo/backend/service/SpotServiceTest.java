package com.famigo.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.famigo.backend.mapper.SpotMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * SpotService は「存在しないスポットの扱い（404）」が重要。
 * ここがズレるとフロントのエラー表示や画面遷移が崩れる。
 */
@ExtendWith(MockitoExtension.class)
class SpotServiceTest {

  @Mock
  private SpotMapper spotMapper;

  private SpotService sut;

  @BeforeEach
  void before() {
    sut = new SpotService(spotMapper);
  }

  @Test
  void スポット詳細_存在しないIDは404になること() {
    when(spotMapper.findDetailById(999999L, null)).thenReturn(null);

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> sut.getSpotDetail(999999L, null));

    assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
  }
}
