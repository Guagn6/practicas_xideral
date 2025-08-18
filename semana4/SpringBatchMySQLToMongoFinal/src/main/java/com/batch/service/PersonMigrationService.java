package com.batch.service;

import com.batch.entities.mongodb.PersonMongoDB;
import com.batch.entities.mysql.PersonMySQL;
import com.batch.persistence.mongodb.IPersonMongoDBRepository;
import com.batch.persistence.mysql.IPersonMySQLRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PersonMigrationService {

    @Autowired
    private IPersonMySQLRepository mysqlRepository;

    @Autowired
    private IPersonMongoDBRepository mongoRepository;

    // Obtiene estadísticas de la migración
    public MigrationStats getMigrationStats() {
        long totalInMySQL = mysqlRepository.countActivePersons();
        long totalInMongoDB = mongoRepository.countMigratedPersons();

        log.info("Estadísticas - MySQL: {}, MongoDB: {}", totalInMySQL, totalInMongoDB);

        return new MigrationStats(totalInMySQL, totalInMongoDB);
    }

    // Verifica si una persona ya fue migrada
    public boolean isPersonMigrated(Long mysqlId) {
        return mongoRepository.existsByMysqlId(mysqlId);
    }

    // Obtiene una persona de MySQL por ID
    public Optional<PersonMySQL> getPersonFromMySQL(Long id) {
        return mysqlRepository.findById(id);
    }

    // Obtiene una persona migrada de MongoDB por su ID original de MySQL
    public Optional<PersonMongoDB> getMigratedPerson(Long mysqlId) {
        return mongoRepository.findByMysqlId(mysqlId);
    }

    // Obtiene todas las personas activas de MySQL para migrar
    public List<PersonMySQL> getAllActivePersonsFromMySQL() {
        return mysqlRepository.findActivePersons();
    }

    // Clase interna para estadísticas de migración
    public static class MigrationStats {
        private final long mysqlCount;
        private final long mongodbCount;
        private final double migrationPercentage;

        public MigrationStats(long mysqlCount, long mongodbCount) {
            this.mysqlCount = mysqlCount;
            this.mongodbCount = mongodbCount;
            this.migrationPercentage = mysqlCount > 0 ?
                    (double) mongodbCount / mysqlCount * 100 : 0;
        }

        public long getMysqlCount() { return mysqlCount; }
        public long getMongodbCount() { return mongodbCount; }
        public double getMigrationPercentage() { return migrationPercentage; }
        public long getPendingCount() { return mysqlCount - mongodbCount; }

        @Override
        public String toString() {
            return String.format("MigrationStats{MySQL=%d, MongoDB=%d, Percentage=%.2f%%, Pending=%d}",
                    mysqlCount, mongodbCount, migrationPercentage, getPendingCount());
        }
    }
}
