package com.famigo.backend.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.famigo.backend.mapper.FavoriteMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * FavoriteService は薄いService（Mapper呼び出し中心）なので、
 * 「呼び出しが正しい」ことを最小の検証で押さえる。
 */
@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

  @Mock
  private FavoriteMapper favoriteMapper;

  private FavoriteService sut;

  @BeforeEach
  void before() {
    sut = new FavoriteService(favoriteMapper);
  }

  @Test
  void お気に入り登録でMapperが呼ばれること() {
    sut.addFavorite(1L, 2L);
    verify(favoriteMapper, times(1)).upsertFavorite(1L, 2L);
  }

  @Test
  void お気に入り解除でMapperが呼ばれること() {
    sut.removeFavorite(1L, 2L);
    verify(favoriteMapper, times(1)).logicalDeleteFavorite(1L, 2L);
  }

  @Test
  void お気に入り判定でMapperが呼ばれること() {
    sut.isFavorite(1L, 2L);
    verify(favoriteMapper, times(1)).existsActiveFavorite(1L, 2L);
  }

  @Test
  void お気に入り一覧でMapperが呼ばれること() {
    sut.getFavorites(1L);
    verify(favoriteMapper, times(1)).findFavoriteSpots(1L);
  }
}
