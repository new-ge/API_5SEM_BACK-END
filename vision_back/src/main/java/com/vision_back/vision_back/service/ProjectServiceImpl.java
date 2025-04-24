package com.vision_back.vision_back.service;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
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
    public List<TreeMap<String, Object>> listAllProjectsByUser(Integer userId){
        setHeadersProject();

        String url = "https://api.taiga.io/api/v1/projects?member=" + userId;

        System.out.println(" Chamando a API do Taiga para userId: " + userId);
        System.out.println(" URL: " + url);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
            url, 
            HttpMethod.GET,
            headersEntity,
            String.class);

            String body = response.getBody();
            //System.out.println("Corpo da resposta: " + body);

            

            if (body == null || body.isEmpty()) {
                //System.out.println(" Resposta vazia ou nula recebida da API.");
                throw new RuntimeException("A resposta da API está vazia ou nula.");
            }

            List<TreeMap<String,Object>> projects = new ArrayList<>();

            JsonNode root = objectMapper.readTree(response.getBody());

            if (root.isArray()){
                if (root.size() == 0) {
                    //System.out.println(" Nenhum projeto retornado para o usuário.");
                }
                for (JsonNode node : root) {
                    Integer projectId = node.get("id").asInt();
                    String name = node.get("name").asText();
                    
                    //System.out.println("Projeto ID: " + projectId + " | Nome: " + name);

                    TreeMap <String, Object> projectMap = new TreeMap<>();
                    projectMap.put("id", projectId);
                    projectMap.put("name", name);

                    projects.add(projectMap);
                
                } 
            }else {
                //System.out.println("Nenhum projeto encontrado para o usuário.");
            }

                return projects;
                
            } catch (Exception e) {
                //System.err.println("Erro ao fazer requisição para a API: " + e.getMessage());
                e.printStackTrace(); 
                throw new RuntimeException("Erro ao localizar os projetos", e);
            }
            
    }
}


//userId para teste: 754823,758714,754228