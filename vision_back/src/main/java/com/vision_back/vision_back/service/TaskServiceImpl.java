package com.vision_back.vision_back.service;

import java.util.HashMap;
import java.util.Map;
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

import jakarta.validation.constraints.Null;

@Service
public class TaskServiceImpl implements TaskService {
    ObjectMapper objectMapper = new ObjectMapper();
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    VisionBackApplication vba = new VisionBackApplication();
    HttpEntity<Void> headersEntity;

    @Override
    public HttpEntity<Void> setHeadersTasks(Integer projectId, Integer userId) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(vba.functionGetToken());
            
        return new HttpEntity<>(headers);
    }

    @Override
    public Map<String, Integer> countTasksById(Integer projectId, Integer userId) {
        setHeadersTasks(projectId, userId);

        ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/tasks?project="+projectId+"&assigned_to="+userId, HttpMethod.GET, headersEntity, String.class);
        Map<String, Integer> statusCount = new HashMap<>();

        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            for (JsonNode node : rootNode) {
                String nameStatus = node.get("status_extra_info").get("name").asText();
                statusCount.put(nameStatus, statusCount.getOrDefault(nameStatus, 0) + 1);
            }

            return statusCount;

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar User Stories", e);
        }
    }
  
    @Override
    public int countCardsCreatedByDateRange(Integer userId, Integer projectId, String startDate, String endDate) {
        HttpEntity<Void> headersEntity = setHeadersTasks(projectId, userId); 
        
        String url = "https://api.taiga.io/api/v1/tasks?"
                   + "project=" + projectId + "&"
                   + "created_date__gte=" + startDate + "&"
                   + "created_date__lte=" + endDate;
    
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, headersEntity, String.class);
    
        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());
    
            return rootNode.size(); 
    
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar cards criados no período", e);
        }
    }
  
    @Override
    public Map<String, Integer> getTasksPerSprint(Integer userId, Integer projectId) {
        HttpEntity<Void> headersEntity = setHeadersTasks(projectId, userId); 
        Map<String, Integer> tasksPerSprint = new TreeMap<>();

        String sprintUrl = "https://api.taiga.io/api/v1/milestones?project=" + projectId;
        ResponseEntity<String> sprintResponse = restTemplate.exchange(sprintUrl, HttpMethod.GET, headersEntity, String.class);

        try {
            JsonNode sprints = objectMapper.readTree(sprintResponse.getBody());

            for (JsonNode sprint : sprints) {
                String sprintName = sprint.get("name").asText();
                String startDate = sprint.get("estimated_start").asText();
                String endDate = sprint.get("estimated_finish").asText();

                String taskUrl = "https://api.taiga.io/api/v1/tasks?"
                        + "project=" + projectId + "&"
                        + "created_date__gte=" + startDate + "&"
                        + "created_date__lte=" + endDate;

                ResponseEntity<String> taskResponse = restTemplate.exchange(taskUrl, HttpMethod.GET, headersEntity, String.class);
                JsonNode tasks = objectMapper.readTree(taskResponse.getBody());

                tasksPerSprint.put(sprintName, tasks.size());
            }

            return tasksPerSprint;

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar tasks por sprint", e);
        }
    }

    @Override
    public Map<String, Integer> countTasksByStatusClosedBySprint(Integer userId, Integer projectId) {
        HttpEntity<Void> headersEntity = setHeadersTasks(projectId, userId); 
        Map<String, Integer> tasksPerSprint = new TreeMap<>();
        Integer sumClosed = 0;

        String sprintUrl = "https://api.taiga.io/api/v1/milestones?project=" + projectId;
        ResponseEntity<String> sprintResponse = restTemplate.exchange(sprintUrl, HttpMethod.GET, headersEntity, String.class);

        try {
            JsonNode sprints = objectMapper.readTree(sprintResponse.getBody());

            for (JsonNode sprint : sprints) {
                String sprintName = sprint.get("name").asText();
                String startDate = sprint.get("estimated_start").asText();
                String endDate = sprint.get("estimated_finish").asText();

                String taskUrl = "https://api.taiga.io/api/v1/tasks?"
                        + "project=" + projectId + "&"
                        + "assigned_to=" + userId + "&"
                        + "created_date__gte=" + startDate + "&"
                        + "created_date__lte=" + endDate;

                ResponseEntity<String> taskResponse = restTemplate.exchange(taskUrl, HttpMethod.GET, headersEntity, String.class);
                JsonNode tasks = objectMapper.readTree(taskResponse.getBody());

                for (JsonNode node : tasks) {
                    if ((node.get("status_extra_info").get("name").asText()).equals("Closed")){
                        sumClosed += 1;
                    } else { 
                        continue;
                    }
                } 

                tasksPerSprint.put(sprintName, sumClosed);
            }
            System.out.println(tasksPerSprint);

            return tasksPerSprint;

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar tasks por sprint", e);
        }
    }

    @Override
    public Integer countTasksByStatusClosed(Integer projectId, Integer userId, String startDate, String endDate) {
        setHeadersTasks(projectId, userId);

        ResponseEntity<String> response = restTemplate.exchange("https://api.taiga.io/api/v1/tasks?project="+projectId+"&assigned_to="+userId + "&created_date__gte=" + startDate + "&created_date__lte=" + endDate, HttpMethod.GET, headersEntity, String.class);
        Integer sumClosed = 0;

        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            for (JsonNode node : rootNode) {
                if ((node.get("status_extra_info").get("name").asText()).equals("Closed")){
                    sumClosed += 1;
                } else { 
                    continue;
                }
            } 
            return sumClosed;

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar User Stories", e);
        }
    }    
    @Override
    public Map<String, Integer> countTasksByTag(Integer taskId, Integer tagId) {
        setHeadersTasks(taskId, tagId);
    
        // Realizando a chamada à API Taiga
        ResponseEntity<String> response = restTemplate.exchange(
            "https://api.taiga.io/api/v1/tasks?project=" + taskId + "&assigned_to=" + tagId, 
            HttpMethod.GET, 
            headersEntity, 
            String.class
        );
    
        // Mapa para armazenar a contagem das tags
        Map<String, Integer> tagCount = new HashMap<>();
    
        try {
            // Processando a resposta da API
            JsonNode rootNode = objectMapper.readTree(response.getBody());
    
            // Iterando sobre as tarefas retornadas pela API
            for (JsonNode node : rootNode) {
                // Verificando se a tarefa tem tags
                if (node.get("tags") != null) {
                    // Iterando sobre as tags da tarefa (caso seja um array de tags)
                    for (JsonNode tagNode : node.get("tags")) {
                        // Obter o nome da tag ou ID, dependendo da estrutura da API
                        String tagName = tagNode.get("name").asText();  // Usando 'name' para o nome da tag
    
                        // Atualizando a contagem de tags no mapa
                        tagCount.put(tagName, tagCount.getOrDefault(tagName, 0) + 1);
                    }
                }
            }
    
            // Retornando o mapa de contagem das tags após processar todas as tarefas
            return tagCount;
    
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar as User Stories", e);
        }
    }
}

    