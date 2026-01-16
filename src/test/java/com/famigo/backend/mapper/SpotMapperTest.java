package com.famigo.backend.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.famigo.backend.dto.SpotDetailDto;
import com.famigo.backend.dto.SpotListItemDto;
import com.famigo.backend.dto.SpotSearchCondition;
import com.famigo.backend.testsupport.MybatisTestBase;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * SpotMapper はSQLが複雑になりやすい（JOIN/検索条件/設備など）ので
 * まずは「条件なし」「詳細取得」「not found」を押さえる。
 */
@MybatisTest
class SpotMapperTest extends MybatisTestBase {

  @Autowired
  private SpotMapper sut;

  @Test
  void スポット一覧が取得できること_条件なし() {
    // 条件未指定（conditionが空）＋未ログイン（userId=null）でも落ちないことが重要（フロント初期表示に直結）
    SpotSearchCondition condition = new SpotSearchCondition();
    List<SpotListItemDto> result = sut.findAllWithCategoryAndFacilities(condition, null);

    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    assertThat(result.get(0).getId()).isNotNull();
    assertThat(result.get(0).getName()).isNotBlank();
  }

  @Test
  void スポット詳細が取得できること_スポットID1() {
    // seedに1件は入っている想定（もしseed変更ならIDだけ合わせる）
    SpotDetailDto detail = sut.findDetailById(1L, null);

    assertThat(detail).isNotNull();
    assertThat(detail.getId()).isEqualTo(1L);
    assertThat(detail.getName()).isNotBlank();
  }

  @Test
  void 存在しないスポットIDではnullになること() {
    SpotDetailDto detail = sut.findDetailById(999999L, null);
    assertThat(detail).isNull();
  }
}
