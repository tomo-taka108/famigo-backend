package com.famigo.backend.validation;

/**
 * Bean Validation のグループ（検証順序）を定義するためのクラス。
 * 【目的】
 * - 空欄のとき：NotBlank だけを出す
 * - 入力があるとき：Size などの追加チェックを出す
 * これにより「空欄なのに Size のエラーも出る（2重エラー）」を防げます。
 */
public class ValidationGroups {
  public interface First {} // 第1段階のバリデーション（最初に評価する）
  public interface Second {} // 第2段階のバリデーション（First を通った場合だけ評価する）
}
