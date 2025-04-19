package com.vision_back.vision_back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision_back.vision_back.VisionBackApplication;
import com.vision_back.vision_back.configuration.TokenConfiguration;
import com.vision_back.vision_back.entity.ProjectEntity;
import com.vision_back.vision_back.entity.RoleEntity;
import com.vision_back.vision_back.repository.ProjectRepository;
import com.vision_back.vision_back.repository.RoleRepository;
import com.vision_back.vision_back.repository.UserRepository;

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
    private TokenConfiguration tokenDto;

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
    public Integer getSpecificProjectUserRoleId() {
        Integer projectCode = getProjectId();
        Integer memberId = userServiceImpl.getUserId();
        Integer roleCode = null;

        setHeadersProject();
        
        try {
            ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/projects/"+projectCode, HttpMethod.GET, headersEntity, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            // saveOnDatabaseProject(projectCode, jsonNode.get("name").asText());
            ProjectEntity projectEntity = projectRepository.findByProjectCode(projectCode).orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado"));

            for (JsonNode members : jsonNode.get("members")) {
                if (members.get("id").asInt() == memberId) {
                    saveOnDatabaseUsersRole(members.get("role").asInt(), members.get("role_name").asText(), projectEntity);
                    roleCode = members.get("role").asInt();
                } else {
                    continue;
                }
            }
        return roleCode;
        } catch (Exception e) {
            throw new NullPointerException("Resposta não obtida ou resposta inválida.");
        }
    }

    public ProjectEntity saveOnDatabaseProject(Integer projectCode, String projectName) {
        try {
            ProjectEntity projectEntity = new ProjectEntity(projectCode, projectName);
            return projectRepository.save(projectEntity);
        } catch (DataIntegrityViolationException e) {
            return projectRepository.findByProjectCodeAndProjectName(projectCode, projectName)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao recuperar projeto após falha de integridade", e));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível salvar os dados", e);
        }
    }

    public RoleEntity saveOnDatabaseUsersRole(Integer roleCode, String roleName, ProjectEntity projectCode) {
        try {
            RoleEntity roleEntity = new RoleEntity(roleCode, roleName, projectCode);
            return roleRepository.save(roleEntity);
        } catch (DataIntegrityViolationException e) {
            return roleRepository.findByRoleCodeAndRoleName(roleCode, roleName)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao recuperar projeto após falha de integridade", e));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível salvar os dados", e);
        }
    }
}