package journal.lab3_health;

import journal.lab3_health.Core.HealthService;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Lab3HealthApplicationTests {

	@Autowired
	private HealthService healthService;

	@Test
	void contextLoads() {
		assertNotNull(healthService, "HealthService bean should be loaded");
	}
}