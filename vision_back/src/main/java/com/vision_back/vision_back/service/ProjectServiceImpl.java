package com.vision_back.vision_back.service;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision_back.vision_back.VisionBackApplication;
import com.vision_back.vision_back.component.EntityRetryUtils;
import com.vision_back.vision_back.entity.ProjectEntity;
import com.vision_back.vision_back.entity.RoleEntity;
import com.vision_back.vision_back.repository.ProjectRepository;
import com.vision_back.vision_back.repository.RoleRepository;

@Service
public class ProjectServiceImpl implements ProjectService {
    ObjectMapper objectMapper = new ObjectMapper();
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    VisionBackApplication vba = new VisionBackApplication();
    HttpEntity<Void> headersEntity;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository; 

    @Autowired
    private ProjectRepository projectRepository; 

    @Autowired
    private AuthenticationService auth;

    @Autowired
    private UserProjectHelperServiceImpl taigaHelper;

    @Override
    public HttpEntity<Void> setHeadersProject() {
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(auth.getCachedToken());
        headers.set("x-disable-pagination", "true"); 
            
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
            e.printStackTrace();
            throw new NullPointerException("Resposta não obtida ou resposta inválida.");
        }
    }

    @Override
    public List<Integer> processRolesList() {
        Integer projectCode = getProjectId();
        List<Integer> roleEntites = new ArrayList<>();

        setHeadersProject();
        
        try {
            ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/projects/"+projectCode, HttpMethod.GET, headersEntity, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            for (JsonNode members : jsonNode.get("members")) {
                if (!roleRepository.existsByRoleCode(members.get("role").asInt())) {
                    roleEntites.add(members.get("role").asInt());
                } 
            }    
            return roleEntites;        
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Transactional
    @Override
    public void processRoles() {
        Integer projectCode = getProjectId();
        Integer memberId = userService.getUserId();
        List<RoleEntity> roleEntites = new ArrayList<>();

        setHeadersProject();
        
        try {
            ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/projects/"+projectCode, HttpMethod.GET, headersEntity, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            ProjectEntity projectEntity = EntityRetryUtils.retryUntilFound(
                () -> projectRepository.findByProjectCode(projectCode).orElse(null),
                5,
                200,
                "ProjectEntity"
            );

            for (JsonNode members : jsonNode.get("members")) {
                if (members.get("id").asInt() == memberId) {
                    if (!roleRepository.existsByRoleCode(members.get("role").asInt())) {
                        roleEntites.add(new RoleEntity(members.get("role").asInt(), members.get("role_name").asText(), projectEntity));
                    }
                } 
            }            
            roleRepository.saveAll(roleEntites);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public List<Integer> processProjectList() {
        Integer member = taigaHelper.loggedUserId();
        List<Integer> projectEntites = new ArrayList<>();
        setHeadersProject();
        
        try {
            ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/projects?member="+member, HttpMethod.GET, headersEntity, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            for (JsonNode json : jsonNode) {
                if (!projectRepository.existsByProjectCode(json.get("id").asInt())) {
                    projectEntites.add(json.get("id").asInt());
                }
            }
            return projectEntites;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Transactional
    @Override
    public void processProject() {
        Integer memberId = taigaHelper.loggedUserId();
        List<ProjectEntity> projectEntites = new ArrayList<>();
        setHeadersProject();
        
        try {
            ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/projects?member="+memberId, HttpMethod.GET, headersEntity, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            for (JsonNode json : jsonNode) {
                if (!projectRepository.existsByProjectCode(json.get("id").asInt())) {
                    projectEntites.add(new ProjectEntity(json.get("id").asInt(), json.get("name").asText()));
                }
            }
            projectRepository.saveAll(projectEntites);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Integer getProjectId() {
        Integer memberId = taigaHelper.loggedUserId();
        return taigaHelper.fetchProjectIdByUserId(memberId);
    }

    @Override
    public List<TreeMap<String, Object>> listAllProjectsByUser(Integer userCode){
        setHeadersProject();

        String url = "https://api.taiga.io/api/v1/projects?member=" + userCode;
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                url, 
                HttpMethod.GET,
                headersEntity,
                String.class
            );
    
            if (response.getStatusCode() == HttpStatus.OK) {
                String body = response.getBody();
                if (body == null || body.isEmpty()) {
                    return new ArrayList<>();
                }
                List<TreeMap<String, Object>> projects = new ArrayList<>();
                JsonNode root = objectMapper.readTree(body);
    
                if (root.isArray()) {
                    for (JsonNode node : root) {
                        Integer projectId = node.get("id").asInt();
                        String name = node.get("name").asText();
                        TreeMap<String, Object> projectMap = new TreeMap<>();
                        projectMap.put("id", projectId);
                        projectMap.put("name", name);
                        projects.add(projectMap);
                    }
                } 
                return projects;
            } else {
                return new ArrayList<>();
            }
    
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao localizar os projetos", e);
        }
    }

    public Integer getSpecificProjectUserRoleId() {
        Integer projectCode = getProjectId();
        Integer memberId = userService.getUserId();
        Integer roleCode = null;

        setHeadersProject();
        
        try {
            ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/projects/"+projectCode, HttpMethod.GET, headersEntity, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            ProjectEntity projectEntity = EntityRetryUtils.retryUntilFound(
                () -> projectRepository.findByProjectCode(projectCode).orElse(null),
                5,
                200,
                "ProjectEntity"
            );

            for (JsonNode members : jsonNode.get("members")) {
                if (members.get("id").asInt() == memberId) {
                    saveOnDatabaseRole(members.get("role").asInt(), members.get("role_name").asText(), projectEntity);
                    roleCode = members.get("role").asInt();
                } else {
                    continue;
                }
            }
        return roleCode;

        } catch (Exception e) {
            e.printStackTrace();
            throw new NullPointerException("Resposta não obtida ou resposta inválida.");
        }
    }

    @Transactional
    public void saveOnDatabaseProject(Integer projectCode, String projectName) {
        if (!projectRepository.existsByProjectCodeAndProjectName(projectCode, projectName)) {
            ProjectEntity projectEntity = new ProjectEntity(projectCode, projectName);
            projectRepository.save(projectEntity);
        }
    }

    @Transactional
    public void saveOnDatabaseRole(Integer roleCode, String roleName, ProjectEntity projectCode) {
        if (!roleRepository.existsByRoleCodeAndRoleName(roleCode, roleName)) {
            RoleEntity roleEntity = new RoleEntity(roleCode, roleName, projectCode);
            roleRepository.save(roleEntity);
        }
    }
}
