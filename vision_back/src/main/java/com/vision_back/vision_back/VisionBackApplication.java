package com.vision_back.vision_back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vision_back.vision_back.service.AuthenticationServiceImpl;
import com.vision_back.vision_back.service.TaskService;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class VisionBackApplication {
<<<<<<< HEAD

    @Autowired
    private AuthenticationServiceImpl auth;

    @Autowired
    private TaskService processTaskStatsAnMilestone; // injeta pela interface!

    private final Dotenv dotenv = Dotenv.configure().filename("secrets.env").load();

    public static void main(String[] args) {
        SpringApplication.run(VisionBackApplication.class, args);
    }

    @PostConstruct
    public void init() {
        try {
            System.out.println("Obtendo token...");
            auth.getTokenAuthentication(dotenv.get("PASSWORD_SECRET"), dotenv.get("USERNAME_SECRET"));
            System.out.println("Token obtido com sucesso!");

            System.out.println("Processando tarefas...");
            processTaskStatsAnMilestone.processTasksAndStatsAndMilestone();
            System.out.println("Tarefas processadas com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro na inicialização: " + e.getMessage());
        }
    }
=======
	
	@Autowired
	private AuthenticationServiceImpl auth;
	
	public static void main(String[] args) {
		SpringApplication.run(VisionBackApplication.class, args);
	}

>>>>>>> 71c7d93dd61ff73b11b8badc3d6f324d3a2423e3
}
