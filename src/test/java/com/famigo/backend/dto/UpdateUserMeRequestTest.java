package com.famigo.backend.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.famigo.backend.dto.user.UpdateUserMeRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class UpdateUserMeRequestTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void プロフィール更新DTO_新パスワード不一致なら入力チェックに掛かること() {
    UpdateUserMeRequest req = new UpdateUserMeRequest();
    req.setDisplayName("新しい名前");          // 今回はパスワード変更の検証なので必須ではないが、入れてもOK
    req.setEmail("new@example.com");          // 同上

    req.setCurrentPassword("currentpass");    // ★PasswordFirst を通すため必須
    req.setNewPassword("newpass1234");        // ★PasswordSecond のSize(min=6)も通る
    req.setNewPasswordConfirm("DIFF");        // ★ここを不一致にして AssertTrue を落とす

    Set<ConstraintViolation<UpdateUserMeRequest>> violations =
        validator.validate(req, UpdateUserMeRequest.PasswordChange.class); // ★グループ指定が必須

    assertThat(violations).isNotEmpty();
    assertThat(violations)
        .extracting(ConstraintViolation::getMessage)
        .contains("新しいパスワードが一致しません");
  }
}
