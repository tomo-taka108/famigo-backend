package com.famigo.backend.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Getter;

/**
 * エラーレスポンスの統一フォーマット。
 * message は開発者向け。
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

  private final String errorCode;

  private final String message;

  private final List<FieldErrorItem> fieldErrors;

  public ErrorResponse(String errorCode, String message) {
    this.errorCode = errorCode;
    this.message = message;
    this.fieldErrors = null;
  }

  public ErrorResponse(String errorCode, String message, List<FieldErrorItem> fieldErrors) {
    this.errorCode = errorCode;
    this.message = message;
    this.fieldErrors = fieldErrors;
  }

  @Getter
  public static class FieldErrorItem {

    private final String field;

    private final String message;

    public FieldErrorItem(String field, String message) {
      this.field = field;
      this.message = message;
    }
  }

}
