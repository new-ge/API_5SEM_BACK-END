package com.vision_back.vision_back.service;

import org.apache.catalina.connector.Response;
import org.apache.catalina.startup.ClassLoaderFactory.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClient.ResponseSpec;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision_back.vision_back.VisionBackApplication;
import com.vision_back.vision_back.entity.ProjectEntity;
import com.vision_back.vision_back.entity.dto.ProjectDto;
import com.vision_back.vision_back.repository.ProjectRepository;

public class ProjectServiceImpl implements ProjectService {
    ObjectMapper objectMapper = new ObjectMapper();
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    VisionBackApplication vba = new VisionBackApplication();
    HttpEntity<Void> headersEntity;

   @Autowired
   public ProjectRepository projectRepository; 

   @Autowired
   public ProjectEntity projectEntity;

    public HttpEntity<Void> setHeadersProject() {
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(vba.functionGetToken());
            
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

    public Integer getProjectId(Integer memberId) {
        setHeadersProject();
        
        try {
            ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/projects/projects?member="+memberId, HttpMethod.GET, headersEntity, String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            JsonNode getProjectId = jsonNode.get("id");
            return getProjectId.asInt();
        } catch (Exception e) {
            throw new NullPointerException("Resposta não obtida ou resposta inválida.");
        }
    }

    public String getProjectName(Integer memberId) {
        setHeadersProject();
        
        try {
            ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/projects/projects?member="+memberId, HttpMethod.GET, headersEntity, String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            JsonNode getProjectName = jsonNode.get("name");
            return new ObjectMapper().writeValueAsString(getProjectName).replace("\"", "");
        } catch (Exception e) {
            throw new NullPointerException("Resposta não obtida ou resposta inválida.");
        }
    }

    public ProjectEntity saveOnDatabase(Integer projectCode, String projectName) {
        try {
            projectEntity = new ProjectEntity(projectCode, projectName);
            return projectRepository.save(projectEntity);
        } catch (Exception e) {
            throw new ResponseStatusException(Response.SC_REQUESTED_RANGE_NOT_SATISFIABLE, "Não foi possivel salvar os dados", e);
        }
    }
}