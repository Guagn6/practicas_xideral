package com.batch.listeners;

import com.batch.service.PersonMigrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class MigrationJobListener implements JobExecutionListener {

    @Autowired
    private PersonMigrationService migrationService;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("=== INICIANDO JOB DE MIGRACIÓN ===");
        log.info("Job ID: {}", jobExecution.getId());
        log.info("Job Name: {}", jobExecution.getJobInstance().getJobName());
        log.info("Fecha de inicio: {}", LocalDateTime.now());

        try {
            // Obtener estadísticas antes de la migración
            PersonMigrationService.MigrationStats statsBefore = migrationService.getMigrationStats();
            log.info("Estado inicial: {}", statsBefore);

            // Guardar las estadísticas iniciales en el contexto del job
            jobExecution.getExecutionContext().put("initialMysqlCount", statsBefore.getMysqlCount());
            jobExecution.getExecutionContext().put("initialMongodbCount", statsBefore.getMongodbCount());
        } catch (Exception e) {
            log.warn("No se pudieron obtener estadísticas iniciales: {}", e.getMessage());
            // Valores por defecto
            jobExecution.getExecutionContext().put("initialMysqlCount", 0L);
            jobExecution.getExecutionContext().put("initialMongodbCount", 0L);
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("=== FINALIZANDO JOB DE MIGRACIÓN ===");

        try {
            // Calcular duración de forma segura
            long durationSeconds = 0;
            if (jobExecution.getStartTime() != null && jobExecution.getEndTime() != null) {
                LocalDateTime startTime = jobExecution.getStartTime();
                LocalDateTime endTime = jobExecution.getEndTime();
                durationSeconds = (endTime.getSecond() - startTime.getSecond());
            }

            // Obtener estadísticas finales
            PersonMigrationService.MigrationStats statsFinal = null;
            try {
                statsFinal = migrationService.getMigrationStats();
            } catch (Exception e) {
                log.warn("No se pudieron obtener estadísticas finales: {}", e.getMessage());
            }

            // Obtener estadísticas iniciales del contexto
            long initialMysqlCount = 0;
            long initialMongodbCount = 0;
            try {
                initialMysqlCount = jobExecution.getExecutionContext().getLong("initialMysqlCount", 0);
                initialMongodbCount = jobExecution.getExecutionContext().getLong("initialMongodbCount", 0);
            } catch (Exception e) {
                log.warn("No se pudieron recuperar estadísticas iniciales del contexto: {}", e.getMessage());
            }

            // Calcular registros procesados en esta ejecución
            long recordsProcessed = 0;
            if (statsFinal != null) {
                recordsProcessed = statsFinal.getMongodbCount() - initialMongodbCount;
            }

            // Log de información básica
            log.info("Job ID: {}", jobExecution.getId());
            log.info("Estado final: {}", jobExecution.getStatus());
            log.info("Exit Code: {}", jobExecution.getExitStatus().getExitCode());
            log.info("Duración: {} segundos", durationSeconds);
            log.info("Registros procesados en esta ejecución: {}", recordsProcessed);

            if (statsFinal != null) {
                log.info("Estado final de migración: {}", statsFinal);
            }

            // Log de métricas del job
            if (jobExecution.getStepExecutions() != null) {
                jobExecution.getStepExecutions().forEach(stepExecution -> {
                    log.info("Step: {} - Read: {}, Write: {}, Skip: {}",
                            stepExecution.getStepName(),
                            stepExecution.getReadCount(),
                            stepExecution.getWriteCount(),
                            stepExecution.getSkipCount()
                    );
                });
            }

            // Verificar si hubo errores
            if (jobExecution.getStatus().isUnsuccessful()) {
                log.error("El job terminó con errores");
                if (jobExecution.getAllFailureExceptions() != null) {
                    jobExecution.getAllFailureExceptions().forEach(exception ->
                            log.error("Error: {}", exception.getMessage(), exception)
                    );
                }
            } else {
                log.info("✅ Job completado exitosamente");

                // Calcular rendimiento solo si hay datos válidos
                if (recordsProcessed > 0 && durationSeconds > 0) {
                    double recordsPerSecond = (double) recordsProcessed / durationSeconds;
                    log.info("Rendimiento: {:.2f} registros/segundo", recordsPerSecond);
                } else if (recordsProcessed > 0) {
                    log.info("Procesados {} registros (duración muy corta para calcular rendimiento)", recordsProcessed);
                }
            }

        } catch (Exception e) {
            log.error("Error al generar reporte final del job: {}", e.getMessage(), e);
        }

        log.info("=== FIN DEL REPORTE DE MIGRACIÓN ===");
    }
}
