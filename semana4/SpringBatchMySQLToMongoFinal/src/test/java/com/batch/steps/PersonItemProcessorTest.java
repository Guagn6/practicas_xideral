package com.batch.steps;

import com.batch.entities.mongodb.PersonMongoDB;
import com.batch.entities.mysql.PersonMySQL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PersonItemProcessor Tests")
class PersonItemProcessorTest {

    @InjectMocks
    private PersonItemProcessor personItemProcessor;

    private PersonMySQL personMySQL;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
        personMySQL = createValidPersonMySQL();
    }

    private PersonMySQL createValidPersonMySQL() {
        PersonMySQL person = new PersonMySQL();
        person.setId(1L);
        person.setName("Juan");
        person.setLastName("Perez");
        person.setAge(30);
        person.setStatus("ACTIVE");
        person.setCreateAt(testDateTime);
        return person;
    }

    @Test
    @DisplayName("Debe procesar correctamente una persona válida")
    void shouldProcessValidPerson() throws Exception {
        // When
        PersonMongoDB result = personItemProcessor.process(personMySQL);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getMysqlId());
        assertEquals("JUAN", result.getName());
        assertEquals("PEREZ", result.getLastName());
        assertEquals("30", result.getAge());
        assertEquals("ACTIVE", result.getStatus());
        assertEquals(testDateTime, result.getCreateAt());
        assertEquals("MYSQL_BATCH", result.getMigrationSource());
        assertNotNull(result.getMigratedAt());
    }

    @Test
    @DisplayName("Debe retornar null cuando el nombre es null")
    void shouldReturnNullWhenNameIsNull() throws Exception {
        // Given
        personMySQL.setName(null);

        // When
        PersonMongoDB result = personItemProcessor.process(personMySQL);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Debe retornar null cuando el nombre está vacío")
    void shouldReturnNullWhenNameIsEmpty() throws Exception {
        // Given
        personMySQL.setName("");

        // When
        PersonMongoDB result = personItemProcessor.process(personMySQL);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Debe retornar null cuando el nombre solo tiene espacios")
    void shouldReturnNullWhenNameIsOnlySpaces() throws Exception {
        // Given
        personMySQL.setName("   ");

        // When
        PersonMongoDB result = personItemProcessor.process(personMySQL);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Debe limpiar y formatear correctamente el nombre con espacios")
    void shouldCleanAndFormatNameWithSpaces() throws Exception {
        // Given
        personMySQL.setName("  juan  ");
        personMySQL.setLastName("  perez  ");

        // When
        PersonMongoDB result = personItemProcessor.process(personMySQL);

        // Then
        assertNotNull(result);
        assertEquals("JUAN", result.getName());
        assertEquals("PEREZ", result.getLastName());
    }

    @Test
    @DisplayName("Debe manejar correctamente lastName null")
    void shouldHandleNullLastName() throws Exception {
        // Given
        personMySQL.setLastName(null);

        // When
        PersonMongoDB result = personItemProcessor.process(personMySQL);

        // Then
        assertNotNull(result);
        assertEquals("JUAN", result.getName());
        assertNull(result.getLastName());
    }

    @Test
    @DisplayName("Debe convertir edad a string correctamente")
    void shouldConvertAgeToString() throws Exception {
        // Given
        personMySQL.setAge(25);

        // When
        PersonMongoDB result = personItemProcessor.process(personMySQL);

        // Then
        assertNotNull(result);
        assertEquals("25", result.getAge());
    }

    @Test
    @DisplayName("Debe establecer la fecha de migración")
    void shouldSetMigrationDate() throws Exception {
        // Given
        LocalDateTime beforeProcessing = LocalDateTime.now();

        // When
        PersonMongoDB result = personItemProcessor.process(personMySQL);

        // Then
        assertNotNull(result);
        assertNotNull(result.getMigratedAt());
        assertTrue(result.getMigratedAt().isAfter(beforeProcessing.minusSeconds(1)));
    }

    @Test
    @DisplayName("Debe manejar valores extremos de edad")
    void shouldHandleExtremeAgeValues() throws Exception {
        // Given
        personMySQL.setAge(0);

        // When
        PersonMongoDB result1 = personItemProcessor.process(personMySQL);

        // Given
        personMySQL.setAge(150);

        // When
        PersonMongoDB result2 = personItemProcessor.process(personMySQL);

        // Then
        assertEquals("0", result1.getAge());
        assertEquals("150", result2.getAge());
    }

    @Test
    @DisplayName("Debe procesar correctamente con estado diferente a ACTIVE")
    void shouldProcessWithDifferentStatus() throws Exception {
        // Given
        personMySQL.setStatus("INACTIVE");

        // When
        PersonMongoDB result = personItemProcessor.process(personMySQL);

        // Then
        assertNotNull(result);
        assertEquals("INACTIVE", result.getStatus());
    }
}