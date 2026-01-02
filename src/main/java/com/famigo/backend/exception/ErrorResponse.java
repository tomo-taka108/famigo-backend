package com.famigo.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * エラーレスポンスの統一フォーマット。
 * message は開発者向け。
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {

  private String errorCode;

  private String message;

}
