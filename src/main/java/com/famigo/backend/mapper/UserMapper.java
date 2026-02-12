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


  /**
   * users にユーザーを新規作成する。
   * INSERT 後、生成されたIDが user.id にセットされる（useGeneratedKeys）。
   *
   * @param user 登録するユーザー情報
   * @return 影響行数（成功:1）
   */
  int insert(User user);


  /**
   * プロフィール情報を更新する（表示名 + メールアドレスを同時更新）（論理削除ユーザーは対象外）
   * 【用途】
   * - アカウント設定画面の「更新」ボタン
   * - 方針（原子性）：入力エラーがあれば、どの項目も更新しない
   *
   * @param id ユーザーID
   * @param name 新しい表示名
   * @param email 新しいメールアドレス
   * @return 影響行数（成功:1）
   */
  int updateProfile(@Param("id") Long id, @Param("name") String name, @Param("email") String email);


  /**
   * パスワードハッシュ（users.password_hash）を更新する（論理削除ユーザーは対象外）
   *
   * @param id ユーザーID
   * @param passwordHash 新しいパスワードハッシュ
   * @return 影響行数（成功:1）
   */
  int updatePasswordHash(@Param("id") Long id, @Param("passwordHash") String passwordHash);


  /**
   * 退会（論理削除）する。
   * - users.is_deleted を 1 にする
   * - users.name を「退会ユーザー」に更新する
   *
   * @param id ユーザーID
   * @param withdrawnName 退会後の表示名（例："退会ユーザー"）
   * @return 影響行数（成功:1）
   */
  int withdraw(@Param("id") Long id, @Param("withdrawnName") String withdrawnName);

}
