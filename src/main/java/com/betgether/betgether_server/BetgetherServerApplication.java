package com.betgether.betgether_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BetgetherServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BetgetherServerApplication.class, args);
	}

}
