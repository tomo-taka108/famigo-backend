package com.famigo.backend.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * デモアカウントを「更新・退会できない」ように保護するガード。
 * ポートフォリオのデモログインは不特定多数が触る前提のため、
 * demo_user が退会・パスワード変更される事故をバックエンドで確実に防ぐ。
 */
@Component
public class DemoAccountGuard {

  private final Set<String> protectedEmails;

  public DemoAccountGuard(
      @Value("${famigo.demo.protected-emails:}") String protectedEmailsCsv
  ) {
    if (protectedEmailsCsv == null || protectedEmailsCsv.isBlank()) {
      this.protectedEmails = Collections.emptySet();
      return;
    }

    this.protectedEmails = Arrays.stream(protectedEmailsCsv.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .map(String::toLowerCase)
        .collect(Collectors.toUnmodifiableSet());
  }

  public boolean isProtectedEmail(String email) {
    if (email == null) return false;
    return protectedEmails.contains(email.toLowerCase());
  }

  /**
   * デモアカウントだった場合は 403 を投げて「更新系操作」を拒否する。
   */
  public void requireNotProtected(String email) {
    if (isProtectedEmail(email)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          "デモアカウントはアカウント情報を変更できません。"
      );
    }
  }
}
