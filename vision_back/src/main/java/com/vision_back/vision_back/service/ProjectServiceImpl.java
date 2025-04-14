package com.vision_back.vision_back.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision_back.vision_back.VisionBackApplication;
import com.vision_back.vision_back.entity.ProjectEntity;
import com.vision_back.vision_back.entity.RoleEntity;
import com.vision_back.vision_back.entity.StatusEntity;
import com.vision_back.vision_back.entity.TaskEntity;
import com.vision_back.vision_back.entity.UserEntity;
import com.vision_back.vision_back.entity.dto.ProjectDto;
import com.vision_back.vision_back.entity.dto.TokenDto;
import com.vision_back.vision_back.repository.ProjectRepository;
import com.vision_back.vision_back.repository.RoleRepository;
import com.vision_back.vision_back.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ProjectServiceImpl implements ProjectService {
    ObjectMapper objectMapper = new ObjectMapper();
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    VisionBackApplication vba = new VisionBackApplication();
    HttpEntity<Void> headersEntity;
    
    @Autowired
    private UserRepository userRepository; 

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private RoleRepository roleRepository; 

    @Autowired
    private ProjectRepository projectRepository; 

    @Autowired
    private TokenDto tokenDto;

    @Override
    public HttpEntity<Void> setHeadersProject() {
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenDto.getAuthToken());
            
        return headersEntity = new HttpEntity<>(headers);
    }

    @Override
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

    @Override
    public Integer getProjectId() {
        Integer memberId = userServiceImpl.getUserId();
        setHeadersProject();
        
        try {
            ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/projects?member="+memberId, HttpMethod.GET, headersEntity, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody()).get(0);
            saveOnDatabaseProject(jsonNode.get("id").asInt(), jsonNode.get("name").asText());
            return jsonNode.get("id").asInt();
        } catch (Exception e) {
            throw new NullPointerException("Resposta não obtida ou resposta inválida.");
        }
    }

    @Override
    public String getSpecificProjectUserRole() {
        Integer projectCode = getProjectId();
        Integer memberId = userServiceImpl.getUserId();
        String roleName = null;

        setHeadersProject();
        System.out.println("https://api.taiga.io/api/v1/projects/"+projectCode);

        try {
            ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/projects/"+projectCode, HttpMethod.GET, headersEntity, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            saveOnDatabaseProject(projectCode, jsonNode.get("name").asText());
            ProjectEntity projectEntity = projectRepository.findByProjectCode(projectCode).orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado"));

            for (JsonNode members : jsonNode.get("members")) {
                if (members.get("id").asInt() == memberId) {
                    roleName = members.get("role_name").asText();
                    saveOnDatabaseUsersRole(members.get("role").asInt(), roleName, projectEntity);
                } else {
                    continue;
                }
            }
        return roleName;
        } catch (Exception e) {
            throw new NullPointerException("Resposta não obtida ou resposta inválida.");
        }
    }

    public ProjectEntity saveOnDatabaseProject(Integer projectCode, String projectName) {
        try {
            return projectRepository.findByProjectCode(projectCode)
            .orElseGet(() -> {
                ProjectEntity projectEntity = new ProjectEntity(projectCode, projectName);
                return projectRepository.save(projectEntity);
            });
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possivel salvar os dados", e);
        }
    }

    public RoleEntity saveOnDatabaseUsersRole(Integer roleCode, String roleName, ProjectEntity projectCode) {
        try {
            return roleRepository.findByRoleCodeAndRoleName(roleCode, roleName)
            .orElseGet(() -> {
                RoleEntity roleEntity = new RoleEntity(roleCode, roleName, projectCode);
                return roleRepository.save(roleEntity);
            });
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possivel salvar os dados", e);
        }
    }
}