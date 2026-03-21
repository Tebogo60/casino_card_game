package com.cassinocards.cassino_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class CassinoApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CassinoApiApplication.class, args);
	}
}
