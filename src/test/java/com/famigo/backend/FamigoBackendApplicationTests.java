package com.famigo.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 起動確認（コンテキストロード）だけのテスト。
 * プロファイル未指定だと default=local になり
 * CIでは localhost のMySQLに接続しに行って Flyway で落ちる可能性がある。
 * → このテストは DB の中身は不要なので
 * - testプロファイル固定
 * - Flyway無効化
 * - H2でDataSourceだけ成立
 */
@ActiveProfiles("test")
@SpringBootTest(
		properties = {
				"spring.datasource.url=jdbc:h2:mem:famigo_ctx;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
				"spring.datasource.driver-class-name=org.h2.Driver",
				"spring.datasource.username=sa",
				"spring.datasource.password=",
				"spring.flyway.enabled=false"
		})
class FamigoBackendApplicationTests {

	@Test
	void contextLoads() {}
}
