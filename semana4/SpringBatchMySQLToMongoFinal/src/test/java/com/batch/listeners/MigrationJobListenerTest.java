package com.batch.listeners;

import com.batch.service.PersonMigrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.*;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MigrationJobListener Tests")
class MigrationJobListenerTest {

    @MockitoBean
    private PersonMigrationService migrationService;

    @InjectMocks
    private MigrationJobListener migrationJobListener;

    @MockitoBean
    private JobExecution jobExecution;

    @MockitoBean
    private JobInstance jobInstance;

    private ExecutionContext executionContext;
    private PersonMigrationService.MigrationStats mockStats;

    @BeforeEach
    void setUp() {
        // Configurar ExecutionContext real para simplificar
        executionContext = new ExecutionContext();

        // Configurar mocks básicos
        when(jobExecution.getId()).thenReturn(123L);
        when(jobExecution.getJobInstance()).thenReturn(jobInstance);
        when(jobInstance.getJobName()).thenReturn("testMigrationJob");
        when(jobExecution.getExecutionContext()).thenReturn(executionContext);
        when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);

        // Mock stats
        mockStats = mock(PersonMigrationService.MigrationStats.class);
        when(mockStats.getMysqlCount()).thenReturn(100L);
        when(mockStats.getMongodbCount()).thenReturn(50L);
    }

    @Test
    @DisplayName("beforeJob - Debe configurar estadísticas iniciales correctamente")
    void shouldSetupInitialStatsCorrectly() {
        // Given
        when(migrationService.getMigrationStats()).thenReturn(mockStats);

        // When
        migrationJobListener.beforeJob(jobExecution);

        // Then
        verify(migrationService, times(1)).getMigrationStats();
        assertEquals(100L, executionContext.getLong("initialMysqlCount"));
        assertEquals(50L, executionContext.getLong("initialMongodbCount"));
    }

    @Test
    @DisplayName("beforeJob - Debe manejar excepción en obtención de estadísticas")
    void shouldHandleExceptionInBeforeJob() {
        // Given
        when(migrationService.getMigrationStats())
                .thenThrow(new RuntimeException("Database error"));

        // When
        migrationJobListener.beforeJob(jobExecution);

        // Then
        verify(migrationService, times(1)).getMigrationStats();
        assertEquals(0L, executionContext.getLong("initialMysqlCount"));
        assertEquals(0L, executionContext.getLong("initialMongodbCount"));
    }

    @Test
    @DisplayName("afterJob - Debe ejecutarse sin errores para job exitoso")
    void shouldExecuteWithoutErrorsForSuccessfulJob() {
        // Given
        executionContext.put("initialMysqlCount", 100L);
        executionContext.put("initialMongodbCount", 50L);

        PersonMigrationService.MigrationStats finalStats = mock(PersonMigrationService.MigrationStats.class);
        when(finalStats.getMongodbCount()).thenReturn(80L);
        when(migrationService.getMigrationStats()).thenReturn(finalStats);

        // When
        assertDoesNotThrow(() -> migrationJobListener.afterJob(jobExecution));

        // Then
        verify(jobExecution, atLeastOnce()).getStatus();
    }

    @Test
    @DisplayName("afterJob - Debe manejar job fallido correctamente")
    void shouldHandleFailedJobCorrectly() {
        // Given
        when(jobExecution.getStatus()).thenReturn(BatchStatus.FAILED);
        when(jobExecution.getAllFailureExceptions()).thenReturn(java.util.List.of());

        // When
        assertDoesNotThrow(() -> migrationJobListener.afterJob(jobExecution));

        // Then
        verify(jobExecution, times(1)).getStatus();
    }

    @Test
    @DisplayName("afterJob - Debe manejar valores nulos")
    void shouldHandleNullValues() {
        // Given
        when(jobExecution.getStartTime()).thenReturn(null);
        when(jobExecution.getEndTime()).thenReturn(null);
        when(jobExecution.getStepExecutions()).thenReturn(null);

        // When
        assertDoesNotThrow(() -> migrationJobListener.afterJob(jobExecution));

        // Then
        verify(jobExecution).getStartTime();
        verify(jobExecution).getEndTime();
    }

    @Test
    @DisplayName("afterJob - Debe manejar excepción en estadísticas finales")
    void shouldHandleExceptionInFinalStats() {
        // Given
        executionContext.put("initialMysqlCount", 100L);
        executionContext.put("initialMongodbCount", 50L);

        when(migrationService.getMigrationStats())
                .thenThrow(new RuntimeException("Database error"));

        // When
        assertDoesNotThrow(() -> migrationJobListener.afterJob(jobExecution));

        // Then
        verify(migrationService, times(1)).getMigrationStats();
    }

    @Test
    @DisplayName("Debe verificar que el listener implementa correctamente la interfaz")
    void shouldVerifyListenerImplementsInterface() {
        // When & Then
        assertTrue(migrationJobListener instanceof JobExecutionListener);
        assertNotNull(migrationJobListener);
    }
}