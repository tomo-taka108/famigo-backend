package com.famigo.backend.testsupport;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

/**
 * Mapper層テスト用の共通土台
 * 狙い
 * - Testcontainers(MySQL)で実DBを起動
 * - Flywayで migration/seed を流す
 * - Mapperテストを「まとめ実行」しても落ちないようにする
 * 重要ポイント
 * - JUnit(Testcontainers拡張) に @Container を任せると
 *   「テストクラス毎にコンテナが停止」→ Spring Contextキャッシュと衝突しやすい。
 * - さらに @DynamicPropertySource 内で assumeTrue() すると
 *   Context生成が失敗扱いになり、CIで連鎖的にテストが落ちる。
 * → このクラスでは
 *   1) コンテナは JVM内シングルトンで遅延起動（必要になった時だけ start）
 *   2) テスト終了時に勝手に stop されない（= まとめ実行が安定）
 *   3) Docker判定で中断せず、起動に失敗したら原因付きで FAIL
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

    // ここで “assumeTrue(Docker利用可)” は絶対にしない
    // 代わりに、必要なら起動して、失敗したら原因を出して落とす。
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

        // JVM終了時に停止（ローカル/CIどちらでも後始末できる）
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
        // CIで「なぜ起動できないか」を明確にログへ出すため、failで落とす
        Assertions.fail("Testcontainers(MySQL) の起動に失敗しました: " + e.getMessage(), e);
      }
    }
  }
}
