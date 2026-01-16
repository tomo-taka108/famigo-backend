package com.famigo.backend.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.famigo.backend.entity.SpotFacility;
import com.famigo.backend.testsupport.MybatisTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

/**
 * 設備情報は entity を返す設計（DTOではない）。
 * フロントの「設備表示」が壊れていない最低確認として1件取得をテストする。
 */
@WebMvcTest
class SpotFacilityMapperTest extends MybatisTestBase {

  @Autowired
  private SpotFacilityMapper sut;

  @Test
  void スポットIDに紐づく設備情報が取得できること() {
    SpotFacility facility = sut.findBySpotId(1L);

    assertThat(facility).isNotNull();
    assertThat(facility.getSpotId()).isEqualTo(1L);
    }
}

