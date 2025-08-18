package com.batch.config;

import com.batch.entities.mongodb.PersonMongoDB;
import com.batch.entities.mysql.PersonMySQL;
import com.batch.listeners.MigrationJobListener;
import com.batch.steps.PersonItemProcessor;
import com.batch.steps.PersonMySQLItemReader;
import com.batch.steps.PersonMongoDBItemWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@EnableBatchProcessing
public class TransaccionBatchConfig {

    @Autowired
    private PersonMySQLItemReader personMySQLItemReader;

    @Autowired
    private PersonItemProcessor personItemProcessor;

    @Autowired
    private PersonMongoDBItemWriter personMongoDBItemWriter;

    @Autowired
    private MigrationJobListener migrationJobListener;

    // Configuración del Reader que lee de MySQL
    @Bean
    public JpaPagingItemReader<PersonMySQL> mysqlReader() {
        log.info("Configurando MySQL Reader");
        return personMySQLItemReader.createReader();
    }

    // Step que procesa la migración de MySQL a MongoDB
    @Bean
    public Step mysqlToMongodbStep(JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager) {
        log.info("Configurando Step: MySQL a MongoDB");

        return new StepBuilder("mysqlToMongodbStep", jobRepository)
                .<PersonMySQL, PersonMongoDB>chunk(10, transactionManager)
                .reader(mysqlReader())
                .processor(personItemProcessor)
                .writer(personMongoDBItemWriter)
                .allowStartIfComplete(true) // Permite re-ejecutar el step
                .build();
    }

    // Job principal que ejecuta la migración
    @Bean
    public Job mysqlToMongodbJob(JobRepository jobRepository,
                                 Step mysqlToMongodbStep) {
        log.info("Configurando Job: Migración MySQL a MongoDB");

        return new JobBuilder("mysqlToMongodbMigrationJob", jobRepository)
                .listener(migrationJobListener)
                .start(mysqlToMongodbStep)
                .build();
    }

    // Step alternativo con configuración personalizada para manejo de errores
    @Bean
    public Step mysqlToMongodbStepWithErrorHandling(JobRepository jobRepository,
                                                    PlatformTransactionManager transactionManager) {
        return new StepBuilder("mysqlToMongodbStepWithErrors", jobRepository)
                .<PersonMySQL, PersonMongoDB>chunk(5, transactionManager) // Chunks más pequeños
                .reader(mysqlReader())
                .processor(personItemProcessor)
                .writer(personMongoDBItemWriter)
                .faultTolerant()
                .skipLimit(10) // Permitir hasta 10 errores
                .skip(Exception.class) // Saltar excepciones generales
                .retryLimit(3) // Reintentar hasta 3 veces
                .retry(Exception.class)
                .build();
    }

    // Job con manejo de errores
    @Bean
    public Job mysqlToMongodbJobWithErrorHandling(JobRepository jobRepository,
                                                  Step mysqlToMongodbStepWithErrorHandling) {
        return new JobBuilder("mysqlToMongodbMigrationJobWithErrors", jobRepository)
                .start(mysqlToMongodbStepWithErrorHandling)
                .build();
    }
}
