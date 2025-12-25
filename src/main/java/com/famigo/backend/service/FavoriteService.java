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

  // ログイン未実装のため、MVPでは「固定ユーザー（例：id=1）」としてお気に入りを扱う
  private static final Long DEMO_USER_ID = 1L;

  /**
   * お気に入り登録（すでに存在する場合は復活）
   *
   * @param spotId スポットID
   */
  public void addFavorite(Long spotId) {
    favoriteMapper.upsertFavorite(DEMO_USER_ID, spotId);
  }

  /**
   * お気に入り解除（論理削除）
   *
   * @param spotId スポットID
   */
  public void removeFavorite(Long spotId) {
    favoriteMapper.logicalDeleteFavorite(DEMO_USER_ID, spotId);
  }

  /**
   * お気に入り済みか判定
   *
   * @param spotId スポットID
   * @return true:お気に入り済み / false:未お気に入り
   */
  public boolean isFavorite(Long spotId) {
    return favoriteMapper.existsActiveFavorite(DEMO_USER_ID, spotId);
  }

  /**
   * お気に入り一覧を取得
   *
   * @return お気に入りスポット一覧（SpotListItemDto のリスト）
   */
  public List<SpotListItemDto> getFavorites() {
    return favoriteMapper.findFavoriteSpots(DEMO_USER_ID);
  }

}
