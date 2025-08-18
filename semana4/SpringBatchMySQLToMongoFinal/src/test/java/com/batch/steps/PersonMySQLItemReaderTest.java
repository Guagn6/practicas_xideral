package com.batch.steps;

import com.batch.entities.mysql.PersonMySQL;
import com.batch.persistence.mysql.IPersonMySQLRepository;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PersonMySQLItemReader Tests")
class PersonMySQLItemReaderTest {

    @MockitoBean
    private EntityManagerFactory entityManagerFactory;

    @MockitoBean
    private IPersonMySQLRepository personRepository;

    @InjectMocks
    private PersonMySQLItemReader personMySQLItemReader;

    @BeforeEach
    void setUp() {
        // Setup básico - no es necesario configurar comportamiento específico
        // ya que estamos probando la creación de objetos, no su funcionalidad
    }

    @Test
    @DisplayName("Debe crear un reader básico correctamente")
    void shouldCreateBasicReader() {
        // When
        JpaPagingItemReader<PersonMySQL> reader = personMySQLItemReader.createReader();

        // Then
        assertNotNull(reader);
        assertEquals("personMySQLReader", reader.getName());
    }

    @Test
    @DisplayName("Debe crear un reader personalizado correctamente")
    void shouldCreateCustomReader() {
        // When
        JpaPagingItemReader<PersonMySQL> reader = personMySQLItemReader.createReaderWithCustomQuery();

        // Then
        assertNotNull(reader);
        assertEquals("personMySQLCustomReader", reader.getName());
    }

    @Test
    @DisplayName("Los readers deben tener nombres diferentes")
    void shouldHaveDifferentReaderNames() {
        // When
        JpaPagingItemReader<PersonMySQL> basicReader = personMySQLItemReader.createReader();
        JpaPagingItemReader<PersonMySQL> customReader = personMySQLItemReader.createReaderWithCustomQuery();

        // Then
        assertNotNull(basicReader);
        assertNotNull(customReader);
        assertNotEquals(basicReader.getName(), customReader.getName());
    }

    @Test
    @DisplayName("Debe crear múltiples instancias de readers independientes")
    void shouldCreateMultipleIndependentReaderInstances() {
        // When
        JpaPagingItemReader<PersonMySQL> reader1 = personMySQLItemReader.createReader();
        JpaPagingItemReader<PersonMySQL> reader2 = personMySQLItemReader.createReader();

        // Then
        assertNotNull(reader1);
        assertNotNull(reader2);
        assertNotEquals(reader1, reader2); // Diferentes instancias
        assertEquals(reader1.getName(), reader2.getName()); // Mismo nombre
    }

    @Test
    @DisplayName("Debe crear readers personalizados independientes")
    void shouldCreateMultipleIndependentCustomReaderInstances() {
        // When
        JpaPagingItemReader<PersonMySQL> customReader1 = personMySQLItemReader.createReaderWithCustomQuery();
        JpaPagingItemReader<PersonMySQL> customReader2 = personMySQLItemReader.createReaderWithCustomQuery();

        // Then
        assertNotNull(customReader1);
        assertNotNull(customReader2);
        assertNotEquals(customReader1, customReader2); // Diferentes instancias
        assertEquals(customReader1.getName(), customReader2.getName()); // Mismo nombre
    }

    @Test
    @DisplayName("Debe verificar que la clase está correctamente configurada")
    void shouldVerifyClassIsProperlyConfigured() {
        // When & Then
        assertNotNull(personMySQLItemReader);
        assertNotNull(entityManagerFactory); // Mock inyectado
        assertNotNull(personRepository); // Mock inyectado
    }

    @Test
    @DisplayName("Debe crear readers con configuración básica válida")
    void shouldCreateReadersWithValidBasicConfiguration() {
        // When
        JpaPagingItemReader<PersonMySQL> basicReader = personMySQLItemReader.createReader();
        JpaPagingItemReader<PersonMySQL> customReader = personMySQLItemReader.createReaderWithCustomQuery();

        // Then
        assertNotNull(basicReader);
        assertNotNull(customReader);

        // Verificar que los nombres están configurados
        assertFalse(basicReader.getName().isEmpty());
        assertFalse(customReader.getName().isEmpty());

        // Verificar que son diferentes tipos de readers
        assertTrue(basicReader.getName().contains("MySQL"));
        assertTrue(customReader.getName().contains("MySQL"));
        assertTrue(customReader.getName().contains("Custom"));
    }

    @Test
    @DisplayName("Debe manejar creación de múltiples readers consecutivos")
    void shouldHandleMultipleConsecutiveReaderCreation() {
        // When
        for (int i = 0; i < 5; i++) {
            JpaPagingItemReader<PersonMySQL> reader = personMySQLItemReader.createReader();
            assertNotNull(reader);
            assertEquals("personMySQLReader", reader.getName());
        }

        for (int i = 0; i < 5; i++) {
            JpaPagingItemReader<PersonMySQL> customReader = personMySQLItemReader.createReaderWithCustomQuery();
            assertNotNull(customReader);
            assertEquals("personMySQLCustomReader", customReader.getName());
        }

        // Then - No hay verificaciones adicionales necesarias,
        // el hecho de que no lance excepciones es suficiente
        assertTrue(true);
    }

    @Test
    @DisplayName("Debe verificar que el EntityManagerFactory está disponible")
    void shouldVerifyEntityManagerFactoryIsAvailable() {
        // Given
        assertNotNull(entityManagerFactory);

        // When
        JpaPagingItemReader<PersonMySQL> reader = personMySQLItemReader.createReader();

        // Then
        assertNotNull(reader);
        // El reader debería haberse creado sin errores incluso con un mock
    }
}