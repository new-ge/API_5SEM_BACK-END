package com.vision_back.vision_back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vision_back.vision_back.service.AuthenticationServiceImpl;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class VisionBackApplication {
	
	@Autowired
	private AuthenticationServiceImpl auth;

	Dotenv dotenv = Dotenv.configure().filename("secrets.env").load();
	public static void main(String[] args) {
		SpringApplication.run(VisionBackApplication.class, args);
	}

	@PostConstruct
	public void functionGetToken() {
		auth.getTokenAuthentication(dotenv.get("PASSWORD_SECRET"), dotenv.get("USERNAME_SECRET"));
	}
}
