package com.famigo.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.famigo.backend.dto.user.UpdateUserMeRequest;
import com.famigo.backend.entity.User;
import com.famigo.backend.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

/**
 * User自己管理（profile/email/password/withdraw）は
 * 認証と直結しているので「失敗時のステータス」が重要。
 */
@ExtendWith(MockitoExtension.class)
class UserSelfServiceTest {

  @Mock
  private UserMapper userMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  private UserSelfService sut;

  @BeforeEach
  void before() {
    sut = new UserSelfService(userMapper, passwordEncoder);
  }

  @Test
  void 退会_有効ユーザーならwithdrawが呼ばれること() {
    User user = new User();
    user.setId(1L);

    when(userMapper.findActiveById(1L)).thenReturn(user);
    when(userMapper.withdraw(eq(1L), anyString())).thenReturn(1);

    sut.withdraw(1L);

    // withdraw の内部で「退会ユーザー名」を付与してupdateする仕様
    verify(userMapper, times(1)).withdraw(eq(1L), anyString());
  }

  @Test
  void パスワード変更_現在パスワード不一致は401になること() {
    User user = new User();
    user.setId(1L);
    user.setPasswordHash("hashed");

    when(userMapper.findActiveById(1L)).thenReturn(user);

    UpdateUserMeRequest request = new UpdateUserMeRequest(
        null,                 // displayName（このメソッドでは未使用）
        null,                 // email（このメソッドでは未使用）
        "wrongwrong",         // currentPassword
        "newnew",             // newPassword
        "newnew"              // newPasswordConfirm（Serviceでは一致チェックしない。Controllerのバリデーション想定）
    );

    when(passwordEncoder.matches("wrongwrong", "hashed")).thenReturn(false);

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> sut.changePassword(1L,request));

    assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
  }
}
