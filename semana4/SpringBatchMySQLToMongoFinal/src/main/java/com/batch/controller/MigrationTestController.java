package com.batch.controller;

import com.batch.service.PersonMigrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/migration")
public class MigrationTestController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("mysqlToMongodbJob")
    private Job migrationJob;

    @Autowired
    private PersonMigrationService migrationService;

    // Endpoint para obtener estadísticas de migración
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getMigrationStats() {
        try {
            PersonMigrationService.MigrationStats stats = migrationService.getMigrationStats();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("mysqlRecords", stats.getMysqlCount());
            response.put("mongodbRecords", stats.getMongodbCount());
            response.put("migrationPercentage", stats.getMigrationPercentage());
            response.put("pendingRecords", stats.getPendingCount());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al obtener estadísticas: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Endpoint para ejecutar la migración manualmente
    @PostMapping("/run")
    public ResponseEntity<Map<String, Object>> runMigration() {
        try {
            log.info("Ejecutando migración manual a través de API");

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("jobName", "manualMigration")
                    .addLong("timestamp", System.currentTimeMillis())
                    .addDate("executionDate", new Date())
                    .addString("executedBy", "API")
                    .toJobParameters();

            var jobExecution = jobLauncher.run(migrationJob, jobParameters);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("jobExecutionId", jobExecution.getId());
            response.put("status", jobExecution.getStatus().toString());
            response.put("startTime", jobExecution.getStartTime());
            response.put("message", "Migración iniciada correctamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al ejecutar migración: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Endpoint para verificar el estado de las bases de datos
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Verificar MySQL
            long mysqlCount = migrationService.getMigrationStats().getMysqlCount();
            response.put("mysql", Map.of(
                    "status", "OK",
                    "recordCount", mysqlCount
            ));
        } catch (Exception e) {
            response.put("mysql", Map.of(
                    "status", "ERROR",
                    "error", e.getMessage()
            ));
        }

        try {
            // Verificar MongoDB
            long mongoCount = migrationService.getMigrationStats().getMongodbCount();
            response.put("mongodb", Map.of(
                    "status", "OK",
                    "recordCount", mongoCount
            ));
        } catch (Exception e) {
            response.put("mongodb", Map.of(
                    "status", "ERROR",
                    "error", e.getMessage()
            ));
        }

        response.put("timestamp", new Date());
        return ResponseEntity.ok(response);
    }
}
