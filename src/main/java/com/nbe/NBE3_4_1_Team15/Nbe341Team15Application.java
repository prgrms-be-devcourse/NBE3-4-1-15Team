package com.nbe.NBE3_4_1_Team15;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Nbe341Team15Application {

	public static void main(String[] args) {
		SpringApplication.run(Nbe341Team15Application.class, args);
	}

}