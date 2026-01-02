package com.famigo.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * アプリ全体の例外をJSON形式（errorCode/message）に統一する。
 * 401/403 は Spring Security 側（EntryPoint / Handler）で返す想定だが、
 * Service層の ResponseStatusException 等もここで補完する。
 */
@RestControllerAdvice //アプリ全体のエラー処理を1か所にまとめるためのアノテーション
public class GlobalExceptionHandler {


  /**
   * バリデーションエラー（@Valid の body）を 400 で返す。
   *
   * @param ex MethodArgumentNotValidException(Spring MVCが標準装備している「@Valid付きの引数が不正だった」専用の例外クラス）
   * @return ErrorResponse（VALIDATION_ERROR）
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

    String detail = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .collect(Collectors.joining("; "));

    ErrorResponse body = new ErrorResponse(
        ErrorCode.VALIDATION_ERROR.name(),
        "Validation failed: " + detail
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }


  /**
   * バリデーションエラー（@Validated の query/path など）を 400 で返す。
   *
   * @param ex ConstraintViolationException
   *           (Jakarta Bean Validation が提供する例外で、クエリパラメータやパス変数の制約違反が発生した場合にSpring が自動的にスローする)
   * @return ErrorResponse（VALIDATION_ERROR）
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {

    ErrorResponse body = new ErrorResponse(
        ErrorCode.VALIDATION_ERROR.name(),
        "Validation failed: " + ex.getMessage()
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }


  /**
   * Service層などで明示的に投げた ResponseStatusException を統一形式で返す。
   *
   * @param ex ResponseStatusException(Spring Frameworkが標準装備している「HTTPステータス付きで例外を投げる」専用の例外クラス）
   * @param request HttpServletRequest
   * @return ErrorResponse（ステータスに応じて errorCode を割り当て）
   */
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {

    HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

    ErrorCode code = mapToErrorCode(status);

    String reason = ex.getReason();
    String message = (reason != null && !reason.isBlank())
        ? reason
        : ("Request failed: " + request.getMethod() + " " + request.getRequestURI());

    ErrorResponse body = new ErrorResponse(code.name(), message);

    return ResponseEntity.status(status).body(body);
  }


  /**
   * それ以外の想定外例外は 500 として統一形式で返す。
   *
   * @param ex Exception
   * @param request HttpServletRequest
   * @return ErrorResponse（INTERNAL_ERROR）
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAnyException(Exception ex, HttpServletRequest request) {

    ErrorResponse body = new ErrorResponse(
        ErrorCode.INTERNAL_ERROR.name(),
        "Internal server error: " + request.getMethod() + " " + request.getRequestURI()
    );

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }


  /**
   * HTTPステータス → errorCode の割り当て。
   *
   * @param status HttpStatus
   * @return ErrorCode
   */
  private ErrorCode mapToErrorCode(HttpStatus status) {

    if (status == HttpStatus.BAD_REQUEST) {
      return ErrorCode.VALIDATION_ERROR;    // 400
    }

    if (status == HttpStatus.UNAUTHORIZED) {
      return ErrorCode.AUTH_REQUIRED;    // 401
    }

    if (status == HttpStatus.FORBIDDEN) {
      return ErrorCode.ACCESS_DENIED;    // 403
    }

    if (status == HttpStatus.NOT_FOUND) {
      return ErrorCode.NOT_FOUND;    // 404
    }

    return ErrorCode.INTERNAL_ERROR;    // 500
  }
}
