package journal.backend_hapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class Lab2HealthApplication {

	public static void main(String[] args) {
		SpringApplication.run(Lab2HealthApplication.class, args);
	}

}