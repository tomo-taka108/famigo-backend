package com.famigo.backend.service;

import com.famigo.backend.dto.SpotListItemDto;
import com.famigo.backend.mapper.FavoriteMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteService {

  private final FavoriteMapper favoriteMapper;

  /**
   * お気に入り登録（すでに存在する場合は復活）
   *
   * @param userId ユーザーID（ログインユーザー）
   * @param spotId スポットID
   */
  public void addFavorite(Long userId, Long spotId) {
    favoriteMapper.upsertFavorite(userId, spotId);
  }

  /**
   * お気に入り解除（論理削除）
   *
   * @param userId ユーザーID（ログインユーザー）
   * @param spotId スポットID
   */
  public void removeFavorite(Long userId, Long spotId) {
    favoriteMapper.logicalDeleteFavorite(userId, spotId);
  }

  /**
   * お気に入り済みか判定
   *
   * @param userId ユーザーID（ログインユーザー）
   * @param spotId スポットID
   * @return true:お気に入り済み / false:未お気に入り
   */
  public boolean isFavorite(Long userId, Long spotId) {
    return favoriteMapper.existsActiveFavorite(userId, spotId);
  }

  /**
   * お気に入り一覧を取得
   *
   * @param userId ユーザーID（ログインユーザー）
   * @return お気に入りスポット一覧（SpotListItemDto のリスト）
   */
  public List<SpotListItemDto> getFavorites(Long userId) {
    return favoriteMapper.findFavoriteSpots(userId);
  }

}
