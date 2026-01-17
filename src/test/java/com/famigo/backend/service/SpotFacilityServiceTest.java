package com.famigo.backend.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.famigo.backend.mapper.SpotFacilityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SpotFacilityService は Mapper呼び出しが中心。
 * テストは「呼ぶべきMapperを呼んでるか」だけでOK。
 */
@ExtendWith(MockitoExtension.class)
class SpotFacilityServiceTest {

  @Mock
  private SpotFacilityMapper spotFacilityMapper;

  private SpotFacilityService sut;

  @BeforeEach
  void before() {
    sut = new SpotFacilityService(spotFacilityMapper);
  }

  @Test
  void 設備情報取得でMapperが呼ばれること() {
    sut.getSpotFacilityBySpotId(1L);
    verify(spotFacilityMapper, times(1)).findBySpotId(1L);
  }
}
