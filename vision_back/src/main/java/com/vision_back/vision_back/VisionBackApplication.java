package com.vision_back.vision_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class VisionBackApplication {
	public static void main(String[] args) {
		SpringApplication.run(VisionBackApplication.class, args);
	}
}
