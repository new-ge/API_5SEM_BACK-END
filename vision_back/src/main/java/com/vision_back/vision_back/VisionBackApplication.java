package com.vision_back.vision_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vision_back.vision_back.service.AuthenticationServiceImpl;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class VisionBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(VisionBackApplication.class, args);
	}

	@PostConstruct
	public String functionGetToken() {
		AuthenticationServiceImpl auth = new AuthenticationServiceImpl();
		return auth.getTokenAuthentication({{secrets.PASSWORD}}, {{secrets.USERNAME}});
	}
}
