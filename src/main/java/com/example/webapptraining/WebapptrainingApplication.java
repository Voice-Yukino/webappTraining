package com.example.webapptraining;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;


@SpringBootApplication
@EntityScan("com.example.webapptraining.entity")

public class WebapptrainingApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebapptrainingApplication.class, args);
	}

}
