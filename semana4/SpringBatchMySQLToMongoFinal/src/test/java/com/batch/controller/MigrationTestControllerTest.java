package com.batch.controller;

import com.batch.service.PersonMigrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MigrationTestController.class)
@DisplayName("MigrationTestController Tests")
class MigrationTestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobLauncher jobLauncher;

    @MockitoBean
    private Job migrationJob;

    @MockitoBean
    private PersonMigrationService migrationService;

    private PersonMigrationService.MigrationStats mockStats;
    private JobExecution mockJobExecution;

    @BeforeEach
    void setUp() {
        // Mock MigrationStats usando constructor simple
        mockStats = mock(PersonMigrationService.MigrationStats.class);
        when(mockStats.getMysqlCount()).thenReturn(100L);
        when(mockStats.getMongodbCount()).thenReturn(80L);
        when(mockStats.getMigrationPercentage()).thenReturn(80.0);
        when(mockStats.getPendingCount()).thenReturn(20L);

        // Mock JobExecution
        mockJobExecution = mock(JobExecution.class);
        when(mockJobExecution.getId()).thenReturn(1L);
        when(mockJobExecution.getStatus()).thenReturn(org.springframework.batch.core.BatchStatus.STARTED);
    }

    @Test
    @DisplayName("GET /stats - Debe retornar estadísticas exitosamente")
    void shouldReturnMigrationStatsSuccessfully() throws Exception {
        // Given
        when(migrationService.getMigrationStats()).thenReturn(mockStats);

        // When & Then
        mockMvc.perform(get("/api/migration/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.mysqlRecords").value(100))
                .andExpect(jsonPath("$.mongodbRecords").value(80))
                .andExpect(jsonPath("$.migrationPercentage").value(80.0))
                .andExpect(jsonPath("$.pendingRecords").value(20));

        verify(migrationService, times(1)).getMigrationStats();
    }

    @Test
    @DisplayName("GET /stats - Debe retornar error cuando el servicio falla")
    void shouldReturnErrorWhenServiceFails() throws Exception {
        // Given
        when(migrationService.getMigrationStats())
                .thenThrow(new RuntimeException("Database connection error"));

        // When & Then
        mockMvc.perform(get("/api/migration/stats"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Database connection error"));

        verify(migrationService, times(1)).getMigrationStats();
    }

    @Test
    @DisplayName("POST /run - Debe ejecutar migración exitosamente")
    void shouldRunMigrationSuccessfully() throws Exception {
        // Given
        when(jobLauncher.run(any(), any())).thenReturn(mockJobExecution);

        // When & Then
        mockMvc.perform(post("/api/migration/run"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.jobExecutionId").value(1))
                .andExpect(jsonPath("$.status").value("STARTED"));

        verify(jobLauncher, times(1)).run(any(), any());
    }

    @Test
    @DisplayName("GET /health - Debe retornar estado de salud")
    void shouldReturnHealthStatus() throws Exception {
        // Given
        when(migrationService.getMigrationStats()).thenReturn(mockStats);

        // When & Then
        mockMvc.perform(get("/api/migration/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.mysql.status").value("OK"))
                .andExpect(jsonPath("$.mysql.recordCount").value(100))
                .andExpect(jsonPath("$.mongodb.status").value("OK"))
                .andExpect(jsonPath("$.mongodb.recordCount").value(80));

        verify(migrationService, times(2)).getMigrationStats();
    }

    @Test
    @DisplayName("Debe manejar excepciones generales en health")
    void shouldHandleGeneralExceptionsInHealth() throws Exception {
        // Given
        when(migrationService.getMigrationStats())
                .thenThrow(new RuntimeException("Connection error"));

        // When & Then
        mockMvc.perform(get("/api/migration/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mysql.status").value("ERROR"))
                .andExpect(jsonPath("$.mysql.error").value("Connection error"))
                .andExpect(jsonPath("$.mongodb.status").value("ERROR"))
                .andExpect(jsonPath("$.mongodb.error").value("Connection error"));
    }
}