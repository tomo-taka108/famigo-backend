package com.famigo.backend.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.famigo.backend.dto.CategoryDto;
import com.famigo.backend.testsupport.MybatisTestBase;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * CategoryMapper のSQLが「MySQL + Flyway seed」環境で動くことの確認。
 */
@MybatisTest
class CategoryMapperTest extends MybatisTestBase {

  @Autowired
  private CategoryMapper sut;

  @Test
  void カテゴリ一覧が取得できること() {
    List<CategoryDto> result = sut.selectAll();

    assertThat(result).isNotEmpty();

    // 全カテゴリの必須項目が正しく取得できること
    assertThat(result).allSatisfy(category -> {
      assertThat(category.getId()).isNotNull();
      assertThat(category.getName()).isNotBlank();
    });
  }
}
