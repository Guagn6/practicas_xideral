package com.batch.config;

import com.batch.service.PersonMigrationService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// Configuración específica para tests que proporciona mocks de servicios
@TestConfiguration
public class BatchTestConfiguration {

    @Bean
    @Primary
    public PersonMigrationService.MigrationStats mockMigrationStats() {
        PersonMigrationService.MigrationStats stats = mock(PersonMigrationService.MigrationStats.class);
        when(stats.getMysqlCount()).thenReturn(100L);
        when(stats.getMongodbCount()).thenReturn(80L);
        when(stats.getMigrationPercentage()).thenReturn(80.0);
        when(stats.getPendingCount()).thenReturn(20L);
        return stats;
    }
}
