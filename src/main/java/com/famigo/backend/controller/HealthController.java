package com.famigo.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ALB（Application Load Balancer）などのヘルスチェック用エンドポイント。
 * Spring Actuator を入れずに最小構成で "200 OK" を返すためのもの。
 * ALBのヘルスチェックパスは /health を指定する想定。
 */
@RestController
public class HealthController {

  @GetMapping("/health")
  public String health() {
    return "OK";
  }
}
