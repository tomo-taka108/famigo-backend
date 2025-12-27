package com.famigo.backend.mapper;

import com.famigo.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

  /**
   * email から users を1件取得する（論理削除ユーザーは除外）
   *
   * @param email メールアドレス（ログインID）
   * @return User（見つからない場合は null）
   */
  User findActiveByEmail(@Param("email") String email);

  /**
   * id から users を1件取得する（論理削除ユーザーは除外）
   *
   * @param id ユーザーID
   * @return User（見つからない場合は null）
   */
  User findActiveById(@Param("id") Long id);
}
