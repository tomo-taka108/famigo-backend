package com.famigo.backend.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.famigo.backend.entity.User;
import com.famigo.backend.testsupport.MybatisTestBase;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 認証/ユーザー自己管理の基盤：
 * - findActiveByEmail / findActiveById が正しく動くことが重要。
 */
@MybatisTest
class UserMapperTest extends MybatisTestBase {

  @Autowired
  private UserMapper sut;

  @Test
  void 有効ユーザーをメールで取得できること() {
    // seed想定：test1@example.com が有効ユーザー
    User user = sut.findActiveByEmail("test1@example.com");

    assertThat(user).isNotNull();
    assertThat(user.getEmail()).isEqualTo("test1@example.com");
    assertThat(user.getIsDeleted()).isFalse(); // is_deleted=0 を期待
  }

  @Test
  void 有効ユーザーをIDで取得できること() {
    User user = sut.findActiveById(1L);

    assertThat(user).isNotNull();
    assertThat(user.getId()).isEqualTo(1L);
    assertThat(user.getIsDeleted()).isFalse();
  }
}
