package com.vision_back.vision_back.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private AuthenticationService auth;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @BeforeEach
    void setup() {
        when(auth.getCachedToken()).thenReturn("mocked-token");
    }

    @Test
    void shouldReturnProjectIdBySlug() throws Exception {
        String slug = "my-project";
        String mockedJson = "{\"id\": 12345}";
        String expectedProjectId = "12345";

        ResponseEntity<String> mockedResponse = new ResponseEntity<>(mockedJson, HttpStatus.OK);

        // Configurar comportamento do RestTemplate mockado - Arrange
        when(restTemplate.exchange(
            eq("https://api.taiga.io/api/v1/projects/by_slug?slug=" + slug),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)
        )).thenReturn(mockedResponse);

        // Executa o método - Act
        String result = projectService.getProjectBySlug(slug);

        // Verifica - Assert
        assertEquals(expectedProjectId, result);
    }
}
