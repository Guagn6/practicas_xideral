package com.batch.config;

import com.batch.listeners.MigrationJobListener;
import com.batch.steps.PersonItemProcessor;
import com.batch.steps.PersonMongoDBItemWriter;
import com.batch.steps.PersonMySQLItemReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransaccionBatchConfig Tests")
class TransaccionBatchConfigTest {

    @MockitoBean
    private PersonMySQLItemReader personMySQLItemReader;

    @MockitoBean
    private PersonItemProcessor personItemProcessor;

    @MockitoBean
    private PersonMongoDBItemWriter personMongoDBItemWriter;

    @MockitoBean
    private MigrationJobListener migrationJobListener;

    @MockitoBean
    private JobRepository jobRepository;

    @MockitoBean
    private PlatformTransactionManager transactionManager;

    @MockitoBean
    private JpaPagingItemReader mysqlReader;

    @InjectMocks
    private TransaccionBatchConfig transaccionBatchConfig;

    @Test
    @DisplayName("Debe crear MySQL Reader correctamente")
    void shouldCreateMySQLReaderCorrectly() {
        // Given
        when(personMySQLItemReader.createReader()).thenReturn(mysqlReader);

        // When
        JpaPagingItemReader result = transaccionBatchConfig.mysqlReader();

        // Then
        assertNotNull(result);
        assertEquals(mysqlReader, result);
        verify(personMySQLItemReader, times(1)).createReader();
    }

    @Test
    @DisplayName("Debe verificar configuración de dependencias")
    void shouldVerifyDependencyConfiguration() {
        // When
        when(personMySQLItemReader.createReader()).thenReturn(mysqlReader);

        // Then
        assertNotNull(transaccionBatchConfig);
        assertNotNull(personMySQLItemReader);
        assertNotNull(personItemProcessor);
        assertNotNull(personMongoDBItemWriter);
        assertNotNull(migrationJobListener);

        // Verificar que el reader se puede crear
        JpaPagingItemReader reader = transaccionBatchConfig.mysqlReader();
        assertNotNull(reader);
    }

    @Test
    @DisplayName("Debe validar que los mocks están correctamente configurados")
    void shouldValidateMocksAreProperlyConfigured() {
        // Given - Los mocks ya están configurados por @Mock

        // When - Simulamos una llamada
        when(personMySQLItemReader.createReader()).thenReturn(mysqlReader);
        when(mysqlReader.getName()).thenReturn("testReader");

        // Then - Verificamos que funcionan
        JpaPagingItemReader reader = personMySQLItemReader.createReader();
        assertNotNull(reader);
        assertEquals("testReader", reader.getName());

        verify(personMySQLItemReader).createReader();
        verify(mysqlReader).getName();
    }

    @Test
    @DisplayName("Debe manejar el caso cuando el reader es null")
    void shouldHandleNullReader() {
        // Given
        when(personMySQLItemReader.createReader()).thenReturn(null);

        // When
        JpaPagingItemReader result = transaccionBatchConfig.mysqlReader();

        // Then
        assertNull(result);
        verify(personMySQLItemReader).createReader();
    }

    @Test
    @DisplayName("Debe verificar múltiples llamadas al reader")
    void shouldVerifyMultipleReaderCalls() {
        // Given
        when(personMySQLItemReader.createReader()).thenReturn(mysqlReader);

        // When
        transaccionBatchConfig.mysqlReader();
        transaccionBatchConfig.mysqlReader();

        // Then
        verify(personMySQLItemReader, times(2)).createReader();
    }
}