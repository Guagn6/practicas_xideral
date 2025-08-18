package com.batch.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EntityScan(basePackages = {"com.batch.entities.mysql"})
@EnableJpaRepositories(basePackages = {"com.batch.persistence.mysql"})
@EnableMongoRepositories(basePackages = {"com.batch.persistence.mongodb"})
public class DatabaseConfig {
    // Clase que asegura que Spring configure correctamente
    // los repositorios de MySQL y MongoDB en sus respectivos paquetes
}
