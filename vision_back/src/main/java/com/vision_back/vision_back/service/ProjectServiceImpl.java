package com.vision_back.vision_back.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision_back.vision_back.VisionBackApplication;
import com.vision_back.vision_back.entity.dto.ProjectDto;


@Service
public class ProjectServiceImpl implements ProjectService {
    ObjectMapper objectMapper = new ObjectMapper();
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    VisionBackApplication vba = new VisionBackApplication();
    HttpEntity<Void> headersEntity;

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

    public String getProjectId(Integer memberId) {
        setHeadersProject();
        
        try {
            ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/projects/projects?member="+memberId, HttpMethod.GET, headersEntity, String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            JsonNode getProjectId = jsonNode.get("id");
            return new ObjectMapper().writeValueAsString(getProjectId).replace("\"", "");
        } catch (Exception e) {
            throw new NullPointerException("Resposta não obtida ou resposta inválida.");
        }
    }

    @Override
    public List<ProjectDto> listAllProjects(){
        setHeadersProject();

        String url = "https://api.taiga.io/api/v1/projects";

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                url, 
                HttpMethod.GET,
                headersEntity,
                String.class);

            System.out.println("Resposta da API: " + response.getBody());

            List<ProjectDto> projects = new ArrayList<>();

            JsonNode root = objectMapper.readTree(response.getBody());

            for (JsonNode node : root) {
                Integer id = node.get("id").asInt();
                String name = node.get("name").asText();
                
                System.out.println("Projeto ID: " + id + " | Nome: " + name);

                ProjectDto dto = new ProjectDto(id,id, name);
                projects.add(dto);
            }

            return projects;
        } catch (Exception e){
            throw new RuntimeException("Erro ao localizar os projetos", e);
        }
    }

}