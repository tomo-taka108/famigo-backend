package com.famigo.backend.mapper;

import com.famigo.backend.dto.SpotListItemDto;
import java.util.List;
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

  /**
   * お気に入り一覧（お気に入りしたスポット一覧）を取得
   *
   * @param userId ユーザーID
   * @return お気に入りスポット一覧（SpotListItemDto のリスト）
   */
  List<SpotListItemDto> findFavoriteSpots(
      @Param("userId") Long userId
  );

}
