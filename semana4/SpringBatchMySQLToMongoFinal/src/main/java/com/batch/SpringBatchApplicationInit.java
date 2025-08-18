package com.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@SpringBootApplication
public class SpringBatchApplicationInit {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	@Qualifier("mysqlToMongodbJob")
	private Job migrationJob;

	// Opcional: Job con manejo de errores
	@Autowired
	@Qualifier("mysqlToMongodbJobWithErrorHandling")
	private Job migrationJobWithErrorHandling;

	public static void main(String[] args) {
		log.info("Iniciando aplicación Spring Batch - MySQL to MongoDB Migration");
		SpringApplication.run(SpringBatchApplicationInit.class, args);
	}

	@Bean
	CommandLineRunner init() {
		return args -> {
			log.info("=== INICIANDO MIGRACIÓN DE MYSQL A MONGODB ===");
			log.info("Fecha y hora de inicio: {}", LocalDateTime.now());

			try {
				JobParameters jobParameters = new JobParametersBuilder()
						.addString("jobName", "mysqlToMongodbMigration")
						.addLong("timestamp", System.currentTimeMillis())
						.addDate("executionDate", new Date())
						.addString("executedBy", "BatchSystem")
						.toJobParameters();

				log.info("Ejecutando job de migración con parámetros: {}", jobParameters);

				// Ejecutar el job principal
				var jobExecution = jobLauncher.run(migrationJob, jobParameters);

				log.info("=== RESULTADO DE LA MIGRACIÓN ===");
				log.info("Estado del Job: {}", jobExecution.getStatus());
				log.info("Fecha de inicio: {}", jobExecution.getStartTime());
				log.info("Fecha de fin: {}", jobExecution.getEndTime());
				log.info("Exit Code: {}", jobExecution.getExitStatus().getExitCode());

				if (jobExecution.getExitStatus().getExitDescription() != null) {
					log.info("Descripción: {}", jobExecution.getExitStatus().getExitDescription());
				}

			} catch (Exception e) {
				log.error("Error durante la ejecución del job de migración: {}", e.getMessage(), e);

				// Opcionalmente, intentar con el job que maneja errores
				try {
					log.info("Intentando ejecutar job con manejo de errores...");
					JobParameters errorJobParams = new JobParametersBuilder()
							.addString("jobName", "mysqlToMongodbMigrationWithErrors")
							.addLong("timestamp", System.currentTimeMillis())
							.addDate("executionDate", new Date())
							.addString("executedBy", "BatchSystemErrorRecovery")
							.toJobParameters();

					var errorJobExecution = jobLauncher.run(migrationJobWithErrorHandling, errorJobParams);
					log.info("Job con manejo de errores completado con estado: {}",
							errorJobExecution.getStatus());

				} catch (Exception secondaryException) {
					log.error("También falló el job con manejo de errores: {}",
							secondaryException.getMessage(), secondaryException);
				}
			}

			log.info("=== APLICACIÓN FINALIZADA ===");
		};
	}
}
