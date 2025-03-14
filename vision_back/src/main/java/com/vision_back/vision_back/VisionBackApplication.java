package com.vision_back.vision_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vision_back.vision_back.service.AuthenticationServiceImpl;
import com.vision_back.vision_back.service.ProjectServiceImpl;
import com.vision_back.vision_back.service.UserStoryServiceImpl;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class VisionBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(VisionBackApplication.class, args);
	}

	@PostConstruct
	public String functionGetToken() {
		AuthenticationServiceImpl auth = new AuthenticationServiceImpl();
		//return auth.getTokenAuthentication({{secrets.PASSWORD}}, {{secrets.USERNAME}});
		return auth.getTokenAuthentication("newge.2025", "newgeneration-git");
	}

	@PostConstruct
	public void functionGetUserStories(){
		ProjectServiceImpl project = new ProjectServiceImpl();
		UserStoryServiceImpl userStories = new UserStoryServiceImpl();
		
		System.out.println("\n\n\n\n\n\n\n\n");
		System.out.println(userStories.getUserStories(project.getProjectId("newgeneration-git-teste")));
		System.out.println("\n\n\n\n\n\n\n\n");


	}
}
