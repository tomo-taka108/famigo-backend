package com.famigo.backend.testsupport;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.MySQLContainer;

/**
 * Mapper層テスト用の共通土台
 * - Testcontainers(MySQL)で実DBを起動
 * - Flywayを有効化して migration/seed を流す
 * 重要：
 * JUnit(Testcontainers)拡張にコンテナ停止を任せると「テストクラス毎に停止」される。
 * しかし Spring の ApplicationContext はキャッシュされるため、2クラス目以降が
 * 「停止済みコンテナへのURL」を掴んだままになり Connection refused を起こす。
 * → このクラスでは JUnit 拡張を使わず、DynamicPropertySource で遅延起動し、
 *   JVM内では MySQL コンテナを使い回す。
 */
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(FlywayAutoConfiguration.class)
public abstract class MybatisTestBase {

  private static final MySQLContainer<?> MYSQL =
      new MySQLContainer<>("mysql:8.0.42")
          .withDatabaseName("famigo_test")
          .withUsername("test")
          .withPassword("test");

  @DynamicPropertySource
  static void registerProps(DynamicPropertyRegistry registry) {

    // Docker が使えない環境では Mapper テストをスキップ
    // （以前の @Testcontainers(disabledWithoutDocker = true) 相当）
    assumeTrue(
        DockerClientFactory.instance().isDockerAvailable(),
        "Docker が利用できないため Mapper テストをスキップします"
    );

    // Spring Context 初期化タイミングで 1 回だけ起動し、テストクラス間で停止させない
    startContainerIfNeeded();

    // DataSource（MyBatis用）
    registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
    registry.add("spring.datasource.username", MYSQL::getUsername);
    registry.add("spring.datasource.password", MYSQL::getPassword);
    registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");

    // Flyway（同じDBへ流す）
    registry.add("spring.flyway.url", MYSQL::getJdbcUrl);
    registry.add("spring.flyway.user", MYSQL::getUsername);
    registry.add("spring.flyway.password", MYSQL::getPassword);
  }

  private static void startContainerIfNeeded() {
    if (MYSQL.isRunning()) {
      return;
    }

    synchronized (MybatisTestBase.class) {
      if (MYSQL.isRunning()) {
        return;
      }

      try {
        MYSQL.start();

        // JVM 終了時に停止（CI/ローカルのどちらでも後始末できる）
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
          try {
            if (MYSQL.isRunning()) {
              MYSQL.stop();
            }
          } catch (Exception ignored) {
            // shutdown hook 内は握りつぶす
          }
        }));
      } catch (Exception e) {
        // 起動失敗時に理由がログに出づらいので、明示して落とす
        Assertions.fail("Testcontainers(MySQL) の起動に失敗しました: " + e.getMessage(), e);
      }
    }
  }
}
