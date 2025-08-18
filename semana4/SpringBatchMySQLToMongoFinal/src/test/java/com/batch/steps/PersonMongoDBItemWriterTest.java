package com.batch.steps;

import com.batch.entities.mongodb.PersonMongoDB;
import com.batch.persistence.mongodb.IPersonMongoDBRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PersonMongoDBItemWriter Tests")
class PersonMongoDBItemWriterTest {

    @MockitoBean
    private IPersonMongoDBRepository personMongoRepository;

    @InjectMocks
    private PersonMongoDBItemWriter personMongoDBItemWriter;

    private PersonMongoDB person1;
    private PersonMongoDB person2;
    private PersonMongoDB person3;

    @BeforeEach
    void setUp() {
        person1 = createPersonMongoDB(1L, "JUAN", "PEREZ");
        person2 = createPersonMongoDB(2L, "MARIA", "GARCIA");
        person3 = createPersonMongoDB(3L, "CARLOS", "LOPEZ");
    }

    private PersonMongoDB createPersonMongoDB(Long mysqlId, String name, String lastName) {
        PersonMongoDB person = new PersonMongoDB();
        person.setMysqlId(mysqlId);
        person.setName(name);
        person.setLastName(lastName);
        person.setAge("30");
        person.setStatus("ACTIVE");
        person.setCreateAt(LocalDateTime.now());
        person.setMigratedAt(LocalDateTime.now());
        person.setMigrationSource("MYSQL_BATCH");
        return person;
    }

    @Test
    @DisplayName("Debe escribir correctamente un chunk sin duplicados")
    void shouldWriteChunkWithoutDuplicates() throws Exception {
        // Given
        Chunk<PersonMongoDB> chunk = new Chunk<>(Arrays.asList(person1, person2));
        when(personMongoRepository.existsByMysqlId(1L)).thenReturn(false);
        when(personMongoRepository.existsByMysqlId(2L)).thenReturn(false);
        when(personMongoRepository.saveAll(anyList())).thenReturn(Arrays.asList(person1, person2));

        // When
        personMongoDBItemWriter.write(chunk);

        // Then
        verify(personMongoRepository).existsByMysqlId(1L);
        verify(personMongoRepository).existsByMysqlId(2L);
        verify(personMongoRepository).saveAll(any(List.class));
    }

    @Test
    @DisplayName("Debe omitir personas duplicadas")
    void shouldSkipDuplicatePersons() throws Exception {
        // Given
        Chunk<PersonMongoDB> chunk = new Chunk<>(Arrays.asList(person1, person2, person3));
        when(personMongoRepository.existsByMysqlId(1L)).thenReturn(false);
        when(personMongoRepository.existsByMysqlId(2L)).thenReturn(true); // Duplicado
        when(personMongoRepository.existsByMysqlId(3L)).thenReturn(false);
        when(personMongoRepository.saveAll(anyList())).thenReturn(Arrays.asList(person1, person3));

        // When
        personMongoDBItemWriter.write(chunk);

        // Then
        verify(personMongoRepository).existsByMysqlId(1L);
        verify(personMongoRepository).existsByMysqlId(2L);
        verify(personMongoRepository).existsByMysqlId(3L);
        verify(personMongoRepository).saveAll(any(List.class));
    }

    @Test
    @DisplayName("Debe manejar chunk vacío")
    void shouldHandleEmptyChunk() throws Exception {
        // Given
        Chunk<PersonMongoDB> chunk = new Chunk<>();

        // When
        assertDoesNotThrow(() -> personMongoDBItemWriter.write(chunk));

        // Then
        verify(personMongoRepository, never()).existsByMysqlId(anyLong());
        verify(personMongoRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Debe manejar todos los elementos duplicados")
    void shouldHandleAllDuplicateElements() throws Exception {
        // Given
        Chunk<PersonMongoDB> chunk = new Chunk<>(Arrays.asList(person1, person2));
        when(personMongoRepository.existsByMysqlId(1L)).thenReturn(true);
        when(personMongoRepository.existsByMysqlId(2L)).thenReturn(true);

        // When
        personMongoDBItemWriter.write(chunk);

        // Then
        verify(personMongoRepository).existsByMysqlId(1L);
        verify(personMongoRepository).existsByMysqlId(2L);
        verify(personMongoRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Debe relanzar excepción cuando saveAll falla")
    void shouldRethrowExceptionWhenSaveAllFails() throws Exception {
        // Given
        Chunk<PersonMongoDB> chunk = new Chunk<>(Arrays.asList(person1));
        when(personMongoRepository.existsByMysqlId(1L)).thenReturn(false);
        when(personMongoRepository.saveAll(anyList()))
                .thenThrow(new RuntimeException("Database connection error"));

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            personMongoDBItemWriter.write(chunk);
        });

        assertEquals("Database connection error", exception.getMessage());
        verify(personMongoRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("Debe continuar procesando cuando existsByMysqlId falla para un elemento")
    void shouldContinueProcessingWhenExistsByMysqlIdFailsForOneElement() throws Exception {
        // Given
        Chunk<PersonMongoDB> chunk = new Chunk<>(Arrays.asList(person1, person2));
        when(personMongoRepository.existsByMysqlId(1L)).thenReturn(false);
        when(personMongoRepository.existsByMysqlId(2L))
                .thenThrow(new RuntimeException("Database error"));
        when(personMongoRepository.saveAll(anyList())).thenReturn(Arrays.asList(person1));

        // When
        assertDoesNotThrow(() -> personMongoDBItemWriter.write(chunk));

        // Then
        verify(personMongoRepository).existsByMysqlId(1L);
        verify(personMongoRepository).existsByMysqlId(2L);
        verify(personMongoRepository).saveAll(any(List.class));
    }

    @Test
    @DisplayName("Debe manejar chunk con un solo elemento")
    void shouldHandleSingleElementChunk() throws Exception {
        // Given
        Chunk<PersonMongoDB> chunk = new Chunk<>(Arrays.asList(person1));
        when(personMongoRepository.existsByMysqlId(1L)).thenReturn(false);
        when(personMongoRepository.saveAll(anyList())).thenReturn(Arrays.asList(person1));

        // When
        personMongoDBItemWriter.write(chunk);

        // Then
        verify(personMongoRepository).existsByMysqlId(1L);
        verify(personMongoRepository).saveAll(any(List.class));
    }

    @Test
    @DisplayName("Debe verificar que el writer implementa correctamente la interfaz")
    void shouldVerifyWriterImplementsInterface() {
        // When & Then
        assertNotNull(personMongoDBItemWriter);
        assertTrue(personMongoDBItemWriter instanceof org.springframework.batch.item.ItemWriter);
    }

    @Test
    @DisplayName("Debe manejar múltiples chunks consecutivos")
    void shouldHandleMultipleConsecutiveChunks() throws Exception {
        // Given
        Chunk<PersonMongoDB> chunk1 = new Chunk<>(Arrays.asList(person1));
        Chunk<PersonMongoDB> chunk2 = new Chunk<>(Arrays.asList(person2));

        when(personMongoRepository.existsByMysqlId(anyLong())).thenReturn(false);
        when(personMongoRepository.saveAll(anyList())).thenReturn(Arrays.asList(person1, person2));

        // When
        personMongoDBItemWriter.write(chunk1);
        personMongoDBItemWriter.write(chunk2);

        // Then
        verify(personMongoRepository, times(2)).existsByMysqlId(anyLong());
        verify(personMongoRepository, times(2)).saveAll(any(List.class));
    }
}