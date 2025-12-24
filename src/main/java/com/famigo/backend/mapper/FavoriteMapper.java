package com.famigo.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FavoriteMapper {

  /**
   * お気に入り登録（すでに存在する場合は論理削除を解除して復活）
   *
   * @param userId ユーザーID
   * @param spotId スポットID
   * @return 更新件数
   */
  int upsertFavorite(
      @Param("userId") Long userId,
      @Param("spotId") Long spotId
  );

  /**
   * お気に入り解除（論理削除）
   *
   * @param userId ユーザーID
   * @param spotId スポットID
   * @return 更新件数
   */
  int logicalDeleteFavorite(
      @Param("userId") Long userId,
      @Param("spotId") Long spotId
  );

  /**
   * お気に入り済みかどうかを判定
   *
   * @param userId ユーザーID
   * @param spotId スポットID
   * @return true:お気に入り済み / false:未お気に入り
   */
  boolean existsActiveFavorite(
      @Param("userId") Long userId,
      @Param("spotId") Long spotId
  );

}
