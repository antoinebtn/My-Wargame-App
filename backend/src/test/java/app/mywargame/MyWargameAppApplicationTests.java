package app.mywargame;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = {"POSTGRES_PASSWORD=unused-testcontainers", "APP_JWT_SECRET=test-only-jwt-secret-not-used-anywhere-else"})
class MyWargameAppApplicationTests {

	@Test
	void contextLoads() {
	}

}
