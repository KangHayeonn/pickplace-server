package com.server.pickplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PickplaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PickplaceApplication.class, args);
	}

}
