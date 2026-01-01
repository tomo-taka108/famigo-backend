package com.famigo.backend.service;

import com.famigo.backend.dto.auth.LoginRequest;
import com.famigo.backend.dto.auth.LoginResponse;
import com.famigo.backend.dto.auth.MeResponse;
import com.famigo.backend.entity.User;
import com.famigo.backend.mapper.UserMapper;
import com.famigo.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor   // finalフィールドを引数に持つコンストラクタを自動生成
public class AuthService {

  private final UserMapper userMapper;              // ユーザー情報取得（DBアクセス）
  private final PasswordEncoder passwordEncoder;    // パスワード照合（ハッシュ比較）
  private final JwtTokenProvider jwtTokenProvider;  // JWT生成・有効期限取得など

  /**
   * ログイン処理（メールアドレス＋パスワードで認証し、JWTを発行する）
   * 【流れ】
   *   メールアドレスで「有効なユーザー（論理削除されていない等）」を取得
   *   平文パスワードとDBのハッシュを照合（matches）
   *   OKならJWTを生成し、token + me情報を返す
   *
   * @param request ログイン入力（email / password）
   * @return ログイン結果（JWT + token種別 + 有効期限 + me情報）
   * @throws ResponseStatusException 認証失敗時は 401
   */
  public LoginResponse login(LoginRequest request) {

    User user = userMapper.findActiveByEmail(request.getEmail());

    if (user == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "メールアドレスまたはパスワードが正しくありません");
    }

    boolean ok = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
    // 入力パスワード（平文）とDBのパスワードハッシュを照合（ハッシュ化して比較）

    if (!ok) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "メールアドレスまたはパスワードが正しくありません");
    }

    String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole());
    // JWTを生成（userId / email / role をpayloadに含める）

    MeResponse me = new MeResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    // フロントに返す「ログイン中ユーザー情報」

    return new LoginResponse(
        token,                              // access token 本体
        "Bearer",                           // Authorization: Bearer <token> の "Bearer" 部分
        jwtTokenProvider.getExpiresInSeconds(), // トークン有効期限（秒）
        me                                  // ログインユーザー情報
    );
  }


  /**
   * 自分自身のユーザー情報を取得する（/auth/me 等で使用する）
   * 【ポイント】：
   *   userId はJWTから取り出した値を想定（Controller側で渡す）
   *   存在しない/無効ユーザーなら 401 を返す
   *
   * @param userId JWTから取得したユーザーID
   * @return 自分のユーザー情報（MeResponse）
   * @throws ResponseStatusException ユーザーが存在しない場合は 401
   */
  public MeResponse me(Long userId) {

    User user = userMapper.findActiveById(userId);
    // userIdで「有効なユーザー」を取得

    if (user == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "ユーザーが見つかりません");
    }

    return new MeResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
  }
}
