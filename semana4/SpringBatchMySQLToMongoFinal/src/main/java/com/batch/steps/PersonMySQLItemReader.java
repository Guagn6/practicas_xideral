package com.batch.steps;

import com.batch.entities.mysql.PersonMySQL;
import com.batch.persistence.mysql.IPersonMySQLRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManagerFactory;

@Slf4j
@Component
public class PersonMySQLItemReader {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private IPersonMySQLRepository personRepository;

    public JpaPagingItemReader<PersonMySQL> createReader() {
        log.info("Configurando reader para leer personas desde MySQL");

        return new JpaPagingItemReaderBuilder<PersonMySQL>()
                .name("personMySQLReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT p FROM PersonMySQL p WHERE p.status = 'ACTIVE' ORDER BY p.id")
                .pageSize(10) // Tamaño de página para la consulta
                .build();
    }

    // Método alternativo usando repository personalizado
    public JpaPagingItemReader<PersonMySQL> createReaderWithCustomQuery() {
        log.info("Configurando reader personalizado para MySQL");

        return new JpaPagingItemReaderBuilder<PersonMySQL>()
                .name("personMySQLCustomReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT p FROM PersonMySQL p WHERE p.status = 'ACTIVE' AND p.createAt IS NOT NULL ORDER BY p.id ASC")
                .pageSize(10)
                .build();
    }
}
