package com.famigo.backend.service;

import com.famigo.backend.dto.auth.MeResponse;
import com.famigo.backend.dto.user.UpdateUserMeRequest;
import com.famigo.backend.entity.User;
import com.famigo.backend.mapper.UserMapper;
import com.famigo.backend.security.DemoAccountGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * ログイン中ユーザーの「自己管理」機能（表示名/メールアドレス/パスワード変更、退会）を扱うService。
 */
@Service
@RequiredArgsConstructor
public class UserSelfService {

  private static final String WITHDRAWN_NAME = "退会ユーザー";

  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final DemoAccountGuard demoAccountGuard;

  /**
   * プロフィール（表示名 + メールアドレス）をまとめて更新する。
   * 【用途】
   * - アカウント設定画面の「更新」ボタン
   * 【方針（原子性）】
   * - バリデーションNGの場合、Controller層で 400 を返し、Service層は呼ばれない
   * - DB制約（email UNIQUE）などで失敗した場合も、トランザクションにより更新は確定しない
   *
   * @param userId  ログイン中ユーザーID
   * @param request 更新内容（displayName/email）
   * @return 更新後のログイン中ユーザー情報
   */
  @Transactional
  public MeResponse updateProfile(Long userId, UpdateUserMeRequest request) {

    User user = requireActiveUser(userId);

    // デモアカウントは更新不可（バックエンドで強制）
    demoAccountGuard.requireNotProtected(user.getEmail());

    try {
      int rows = userMapper.updateProfile(userId, request.getDisplayName(), request.getEmail());
      if (rows != 1) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found.");
      }
    } catch (DuplicateKeyException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered.");
    }

    User updated = requireActiveUser(userId);
    return new MeResponse(updated.getId(), updated.getName(), updated.getEmail(), updated.getRole());
  }


  /**
   * 表示名を更新する。
   *
   * @param userId  ログイン中ユーザーID
   * @param request 表示名変更（displayName）
   * @return 更新後のログイン中ユーザー情報
   */
  @Transactional
  public MeResponse updateDisplayName(Long userId, UpdateUserMeRequest request) {

    User user = requireActiveUser(userId);

    // デモアカウントは更新不可（バックエンドで強制）
    demoAccountGuard.requireNotProtected(user.getEmail());

    int rows = userMapper.updateName(userId, request.getDisplayName());
    if (rows != 1) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found.");
    }

    User updated = requireActiveUser(userId);
    return new MeResponse(updated.getId(), updated.getName(), updated.getEmail(), updated.getRole());
  }


  /**
   * メールアドレスを更新する。
   * ※email再利用は今回は対応しない（DBのUNIQUE制約に従い、退会ユーザーのemailも再利用不可）。
   *
   * @param userId  ログイン中ユーザーID
   * @param request メール変更（email）
   * @return 更新後のログイン中ユーザー情報
   */
  @Transactional
  public MeResponse updateEmail(Long userId, UpdateUserMeRequest request) {

    User user = requireActiveUser(userId);

    // デモアカウントは更新不可（バックエンドで強制）
    demoAccountGuard.requireNotProtected(user.getEmail());

    try {
      int rows = userMapper.updateEmail(userId, request.getEmail());
      if (rows != 1) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found.");
      }
    } catch (DuplicateKeyException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered.");
    }

    User updated = requireActiveUser(userId);
    return new MeResponse(updated.getId(), updated.getName(), updated.getEmail(), updated.getRole());
  }


  /**
   * パスワードを変更する。
   *
   * @param userId  ログイン中ユーザーID
   * @param request パスワード変更（currentPassword/newPassword）
   */
  @Transactional
  public void changePassword(Long userId, UpdateUserMeRequest request) {

    User user = requireActiveUser(userId);

    // デモアカウントは更新不可（バックエンドで強制）
    demoAccountGuard.requireNotProtected(user.getEmail());

    boolean ok = passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash());
    if (!ok) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid current password.");
    }

    String hashed = passwordEncoder.encode(request.getNewPassword());

    int rows = userMapper.updatePasswordHash(userId, hashed);
    if (rows != 1) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found.");
    }
  }


  /**
   * 退会（論理削除）する。
   * - users.is_deleted = 1
   * - users.name = "退会ユーザー"
   * ※レビューは残す（reviews.user_id は維持し、表示名だけ退会名に切り替える）。
   *
   * @param userId ログイン中ユーザーID
   */
  @Transactional
  public void withdraw(Long userId) {

    User user = requireActiveUser(userId);

    // デモアカウントは退会不可（バックエンドで強制）
    demoAccountGuard.requireNotProtected(user.getEmail());

    int rows = userMapper.withdraw(userId, WITHDRAWN_NAME);
    if (rows != 1) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found.");
    }
  }


  private User requireActiveUser(Long userId) {
    User user = userMapper.findActiveById(userId);
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found.");
    }
    return user;
  }
}
