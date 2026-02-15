package com.famigo.backend.testsupport;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(FlywayAutoConfiguration.class)
public abstract class MybatisTestBase {

  private static final MySQLContainer<?> MYSQL =
      new MySQLContainer<>("mysql:8.0.42")
          .withDatabaseName("famigo_test")
          .withUsername("test")
          .withPassword("test")
          .withStartupAttempts(3);

  @DynamicPropertySource
  static void registerProps(DynamicPropertyRegistry registry) {

    // GitHub Actions(CI)では Testcontainers を使わず、workflowのMySQL serviceへ接続
    if (isGitHubActions()) {
      String url = envOrFail("TEST_MYSQL_JDBC_URL");
      String user = envOrFail("TEST_MYSQL_USERNAME");
      String pass = envOrFail("TEST_MYSQL_PASSWORD");

      registry.add("spring.datasource.url", () -> url);
      registry.add("spring.datasource.username", () -> user);
      registry.add("spring.datasource.password", () -> pass);
      registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");

      registry.add("spring.flyway.url", () -> url);
      registry.add("spring.flyway.user", () -> user);
      registry.add("spring.flyway.password", () -> pass);
      return;
    }

    // ローカルは今まで通り Testcontainers(MySQL)
    startContainerIfNeeded();

    registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
    registry.add("spring.datasource.username", MYSQL::getUsername);
    registry.add("spring.datasource.password", MYSQL::getPassword);
    registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");

    registry.add("spring.flyway.url", MYSQL::getJdbcUrl);
    registry.add("spring.flyway.user", MYSQL::getUsername);
    registry.add("spring.flyway.password", MYSQL::getPassword);
  }

  private static boolean isGitHubActions() {
    return "true".equalsIgnoreCase(System.getenv("GITHUB_ACTIONS"));
  }

  private static String envOrFail(String key) {
    String v = System.getenv(key);
    if (v == null || v.isBlank()) {
      throw new IllegalStateException("環境変数 " + key + " が未設定です（CIのMySQL接続情報）");
    }
    return v;
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

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
          try {
            if (MYSQL.isRunning()) {
              MYSQL.stop();
            }
          } catch (Exception ignored) {
            // shutdown hook内は握りつぶし
          }
        }));
      } catch (Exception e) {
        Assertions.fail("Testcontainers(MySQL) の起動に失敗しました: " + e.getMessage(), e);
      }
    }
  }
}
