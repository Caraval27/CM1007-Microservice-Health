package journal.lab3_health;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class Lab3HealthApplication {

	public static void main(String[] args) {
		SpringApplication.run(Lab3HealthApplication.class, args);
	}

}