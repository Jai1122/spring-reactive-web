package com.jaiplays.services.otp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories
public class OtpHandlerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OtpHandlerServiceApplication.class, args);
	}

}
