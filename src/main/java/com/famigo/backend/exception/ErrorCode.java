package com.famigo.backend.exception;

/**
 * APIで返す固定エラーコード。
 * フロントエンドはこの errorCode を見て日本語メッセージを出し分ける想定。
 */
public enum ErrorCode {

  VALIDATION_ERROR,   // 400
  AUTH_REQUIRED,      // 401
  ACCESS_DENIED,      // 403
  NOT_FOUND,          // 404
  INTERNAL_ERROR      // 500

}
