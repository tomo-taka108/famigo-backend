package com.famigo.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.famigo.backend.dto.auth.LoginRequest;
import com.famigo.backend.dto.auth.RegisterRequest;
import com.famigo.backend.entity.User;
import com.famigo.backend.mapper.UserMapper;
import com.famigo.backend.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service層は「ビジネスルール」と「例外変換」をテストするのが目的。
 * DBやMyBatisはモックにして、条件分岐を確実に踏む。
 */
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

  @Mock
  private UserMapper userMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtTokenProvider jwtTokenProvider;

  private AuthService sut;

  @BeforeEach
  void before(){
    sut = new AuthService(userMapper,passwordEncoder,jwtTokenProvider);
  }

  @Test
  void ログイン_メールが存在しない場合_401になること(){
    when(userMapper.findActiveByEmail("x@example.com")).thenReturn(null);

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,()->sut.login(new LoginRequest("x@example.com","pass")));

    assertEquals(HttpStatus.UNAUTHORIZED,ex.getStatusCode());
  }

  @Test
  void ログイン_パスワード不一致の場合_401になること() {
    User user = new User();
    user.setId(1L);
    user.setEmail("test1@example.com");
    user.setPasswordHash("hashed");

    when(userMapper.findActiveByEmail("test1@example.com")).thenReturn(user);
    when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> sut.login(new LoginRequest("test1@example.com", "wrong")));

    assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
  }

  @Test
  void 登録_メール重複の場合_409になること() {
    User existing = new User();
    existing.setId(99L);
    when(userMapper.findActiveByEmail("dup@example.com")).thenReturn(existing);

    RegisterRequest req = new RegisterRequest();
    req.setDisplayName("太郎");
    req.setEmail("dup@example.com");
    req.setPassword("pass1234");
    req.setPasswordConfirm("pass1234");

    ResponseStatusException ex = assertThrows(ResponseStatusException.class,
        () -> sut.register(req));

    assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
  }
}

