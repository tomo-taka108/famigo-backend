package com.famigo.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JWT（JSON Web Token）の生成および検証を行うクラス。
 * このクラスの役割：
 *   ログイン成功時に JWT を発行する
 *   リクエストに含まれる JWT を検証し、Claims（中身）を取り出す
 *   JWT の有効期限情報を提供する
 * Famigo では、JWT に以下の情報を埋め込んでいる：
 *   subject : email（ログイン識別子）
 *   uid     : ユーザーID
 *   role    : ロール（USER / ADMIN など）
 * 署名アルゴリズムは HS256（HMAC + SHA-256）を使用。
 */
@Component
public class JwtTokenProvider {

  // JWTの署名・検証に使う秘密鍵（改ざん検知のための鍵）
  private final SecretKey secretKey;

  // JWTの有効期限（ミリ秒）
  private final Long expirationMillis;


  /**
   * JwtTokenProviderのコンストラクタ。
   * application.propertiesに定義されたJWT設定を受け取り、署名用のSecretKeyと有効期限（ミリ秒）を初期化する。
   *
   * @param secret JWT署名用の秘密文字列
   * @param expirationMinutes JWTの有効期限（分）
   */
  public JwtTokenProvider(
      @Value("${famigo.jwt.secret}") String secret,
      @Value("${famigo.jwt.expiration-minutes}") Long expirationMinutes
  ) {
    // secret文字列をHMAC鍵に変換（HS256用）
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationMillis = expirationMinutes * 60 * 1000;
  }


  /**
   * ユーザー情報をもとにJWTを生成する。
   * 主にログイン成功時に呼ばれ、フロントエンドへ返却される。
   * トークンには以下の情報が含まれる。
   *   subject : email
   *   uid     : ユーザーID
   *   role    : ユーザーロール
   *   iat     : 発行時刻
   *   exp     : 有効期限
   *
   * @param userId ユーザーID
   * @param email  メールアドレス（JWT の subject）
   * @param role   ユーザーロール
   * @return 生成された JWT 文字列
   */
  public String generateToken(Long userId, String email, String role) {

    Date now = new Date();
    Date expiry = new Date(now.getTime() + expirationMillis);

    return Jwts.builder()
        // subject：JWTの「主語」。ここでは email を採用
        .subject(email)
        // claim：自作フィールド（uid / role を入れておく）
        .claim("uid", userId)
        .claim("role", role)
        .issuedAt(now)
        .expiration(expiry)
        // 署名：改ざんされていないことを保証する
        .signWith(secretKey)
        .compact();
  }


  /**
   * JWT を検証し、Claims（トークンの中身）を取得する。
   * 署名検証・有効期限チェックを行い、問題なければ JWT に含まれる Claims を返す。
   * 不正なトークンや期限切れの場合は例外が送出される。
   *
   * @param token フロントエンドから送信されたJWT
   * @return JWTに含まれるClaims
   */
  public Claims parseClaims(String token) {
    return Jwts.parser()
        // jjwt 0.13系：verifyWith(secretKey) で検証鍵をセット
        .verifyWith(secretKey)
        .build()
        // 署名検証 + expなども見て、OKならpayload(Claims)を返す
        .parseSignedClaims(token)
        .getPayload();
  }

  /**
   * JWT の有効期限を「秒単位」で返す。
   * フロントエンドに「トークンの残り有効時間」を伝えたい場合などに使用できる。
   *
   * @return 有効期限（秒）
   */
  public Long getExpiresInSeconds() {
    return expirationMillis / 1000;
  }
}
