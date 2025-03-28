package com.vision_back.vision_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vision_back.vision_back.service.AuthenticationServiceImpl;
import com.vision_back.vision_back.service.UserServiceImpl;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class VisionBackApplication {
	Dotenv dotenv = Dotenv.configure().filename("secrets.env").load();
	public static void main(String[] args) {
		SpringApplication.run(VisionBackApplication.class, args);
	}

	@PostConstruct
	public String functionGetToken() {
		AuthenticationServiceImpl auth = new AuthenticationServiceImpl();
		return auth.getTokenAuthentication(dotenv.get("PASSWORD_SECRET"), dotenv.get("USERNAME_SECRET"));
	}

	@PostConstruct
	public void run() {
		UserServiceImpl usImpl = new UserServiceImpl();
		System.out.println(usImpl.getUserId(1641986));
	}
}
