package com.example.chattingservice;

import com.example.chattingservice.exception.ExceptionControllerAdvice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableDiscoveryClient
public class ChattingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChattingServiceApplication.class, args);
	}

}
