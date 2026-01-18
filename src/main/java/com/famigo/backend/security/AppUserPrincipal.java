package com.famigo.backend.security;

import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Spring Security が「ログイン済みユーザー」として扱うユーザー情報クラス。
 * 通常のフォームログインでは username/password を使うが、Famigo では JWT 認証のため、
 * リクエストに付与されたトークンからuserId / email / role を復元して SecurityContext に格納する。
 * このクラスは {@link org.springframework.security.core.Authentication#getPrincipal()}として参照される想定。
 */
@Getter
@AllArgsConstructor
public class AppUserPrincipal implements UserDetails {

  // users.id（DBのユーザーID）
  private Long userId;

  // ログインIDとして扱うメールアドレス（JWTのsubjectにも入れる）
  private String email;

  // 権限ロール（例: USER / ADMIN / GUEST）
  private String role;

  // Spring Security が権限判定に使う GrantedAuthority（例: ROLE_USER）
  private Collection<? extends GrantedAuthority> authorities;


  /**
   * パスワードを返す必要があるインターフェースだが、FamigoはJWT運用でパスワード照合を行わないため未使用。
   *
   * @return 常に null
   */
  @Override
  public String getPassword() {
    return null; // JWT運用のため利用しない
  }

  /**
   * Spring Security がユーザー名として扱う値。
   * Famigoでは email を username 相当として扱う。
   *
   * @return email
   */
  @Override
  public String getUsername() {
    return email;
  }

  /** @return 常に true（期限切れ管理は現状未実装） */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /** @return 常に true（アカウントロックは現状未実装） */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /** @return 常に true（資格情報期限管理は現状未実装） */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /** @return 常に true（無効化フラグ運用は現状未実装） */
  @Override
  public boolean isEnabled() {
    return true;
  }
}
