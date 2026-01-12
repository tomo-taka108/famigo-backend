package com.famigo.backend.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.famigo.backend.dto.auth.RegisterRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RegisterRequestTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void 登録DTO_適切な値なら入力チェックに異常が発生しないこと() {
    RegisterRequest req = new RegisterRequest();
    req.setDisplayName("テスト太郎");
    req.setEmail("taro@example.com");
    req.setPassword("pass1234");
    req.setPasswordConfirm("pass1234");

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
    assertThat(violations).isEmpty();
  }

  @Test
  void 登録DTO_パスワード不一致なら入力チェックに掛かること() {
    RegisterRequest req = new RegisterRequest();
    req.setDisplayName("テスト太郎");
    req.setEmail("taro@example.com");
    req.setPassword("pass1234");
    req.setPasswordConfirm("DIFF");

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);

    assertThat(violations).isNotEmpty();
    assertThat(violations)
        .extracting(ConstraintViolation::getMessage)
        .contains("パスワードが一致しません");
  }

}