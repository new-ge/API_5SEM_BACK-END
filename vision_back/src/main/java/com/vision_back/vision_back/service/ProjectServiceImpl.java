package com.vision_back.vision_back.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.connector.Response;
import org.apache.catalina.startup.ClassLoaderFactory.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClient.ResponseSpec;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision_back.vision_back.VisionBackApplication;
import com.vision_back.vision_back.entity.ProjectEntity;
import com.vision_back.vision_back.entity.dto.ProjectDto;
import com.vision_back.vision_back.entity.dto.TokenDto;
import com.vision_back.vision_back.repository.ProjectRepository;

@Service
public class ProjectServiceImpl implements ProjectService {
    ObjectMapper objectMapper = new ObjectMapper();
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    VisionBackApplication vba = new VisionBackApplication();
    HttpEntity<Void> headersEntity;
    
    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private ProjectRepository projectRepository; 

    @Autowired
    private TokenDto tokenDto;

    public HttpEntity<Void> setHeadersProject() {
        System.out.println(tokenDto.getAuthToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenDto.getAuthToken());
            
        return headersEntity = new HttpEntity<>(headers);
    }
        
    public String getProjectBySlug(String slugProject) {
        setHeadersProject();
        
        try {
            ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/projects/by_slug?slug="+slugProject, HttpMethod.GET, headersEntity, String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            JsonNode getProjectId = jsonNode.get("id");
            return new ObjectMapper().writeValueAsString(getProjectId).replace("\"", "");
        } catch (Exception e) {
            throw new NullPointerException("Resposta não obtida ou resposta inválida.");
        }
    }

    public Integer getProjectId() {
        Integer memberId = userServiceImpl.getUserId();
        setHeadersProject();
        
        try {
            ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/projects?member="+memberId, HttpMethod.GET, headersEntity, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            saveOnDatabase(jsonNode.get("id").asInt(), jsonNode.get("name").asText());
            return jsonNode.get("id").asInt();
        } catch (Exception e) {
            throw new NullPointerException("Resposta não obtida ou resposta inválida.");
        }
    }

    public String getProjectName(Integer memberId) {
        setHeadersProject();
        
        try {
            ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/projects?member="+memberId, HttpMethod.GET, headersEntity, String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            JsonNode getProjectName = jsonNode.get("name");
            return new ObjectMapper().writeValueAsString(getProjectName).replace("\"", "");
        } catch (Exception e) {
            throw new NullPointerException("Resposta não obtida ou resposta inválida.");
        }
    }

    public ProjectEntity saveOnDatabase(Integer projectCode, String projectName) {
        try {
            ProjectEntity projectEntity = new ProjectEntity(projectCode, projectName);
            return projectRepository.save(projectEntity);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possivel salvar os dados", e);
        }
    }
}