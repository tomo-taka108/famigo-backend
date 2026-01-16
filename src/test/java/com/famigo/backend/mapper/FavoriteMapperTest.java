package com.famigo.backend.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.famigo.backend.testsupport.MybatisTestBase;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * favorites は upsert + logical delete で実装されているため、
 * 「登録→存在判定→解除→存在判定」を最小で押さえる。
 */
@MybatisTest
class FavoriteMapperTest extends MybatisTestBase {

  @Autowired
  private FavoriteMapper sut;

  @Test
  void お気に入り登録と解除ができること() {
    Long userId = 1L;
    Long spotId = 1L;

    // いったん解除（すでに登録済みでもOKな状態を作る）
    sut.logicalDeleteFavorite(userId, spotId);

    // 登録（upsert）
    sut.upsertFavorite(userId, spotId);
    assertThat(sut.existsActiveFavorite(userId, spotId)).isTrue();

    // 解除（論理削除）
    sut.logicalDeleteFavorite(userId, spotId);
    assertThat(sut.existsActiveFavorite(userId, spotId)).isFalse();
  }
}
