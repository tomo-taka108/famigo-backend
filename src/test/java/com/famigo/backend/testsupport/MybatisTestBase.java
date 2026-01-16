package com.famigo.backend.testsupport;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Mapper層テスト用の共通土台
 * - Testcontainers(MySQL)で実DBを起動
 * - Flywayを有効化して migration/seed を流す
 */
@ActiveProfiles("test") // Spring Boot の profile を test に切り替える。application-test.properties があればそれが優先される。
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // H2等に置き換えず、TestcontainersのDBを使う
@ImportAutoConfiguration(FlywayAutoConfiguration.class) // テストでもFlywayを有効化（migrationを流す）
public abstract class MybatisTestBase {

  private static final MySQLContainer<?> MYSQL =
      new MySQLContainer<>("mysql:8.0.42")
          .withDatabaseName("famigo_test")
          .withUsername("test")
          .withPassword("test");

  /**
   * クラスロード時に1回だけコンテナを起動する
   *（テストを何クラスまとめ実行しても、同一コンテナを使い回す）
   */
  static {
    MYSQL.start();
  }

  /**
   * Springのプロパティに「起動したコンテナの接続情報」を注入する。
   * ここをやることで application-test.properties にDB接続を書かなくて良い。
   */
  @DynamicPropertySource // テスト起動時にSpringのプロパティを動的に差し替える（＝コンテナの接続先を注入）
  static void registerProps(DynamicPropertyRegistry registry) {

    // DataSource：MyBatisが使う接続先（毎回コンテナのJDBC URLが変わるので動的に入れる）
    registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
    registry.add("spring.datasource.username", MYSQL::getUsername);
    registry.add("spring.datasource.password", MYSQL::getPassword);
    registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");

    // Flywayも同じDBに向ける（migration/seedをDataSourceと同一コンテナに流すため）
    registry.add("spring.flyway.url", MYSQL::getJdbcUrl);
    registry.add("spring.flyway.user", MYSQL::getUsername);
    registry.add("spring.flyway.password", MYSQL::getPassword);
  }
}
