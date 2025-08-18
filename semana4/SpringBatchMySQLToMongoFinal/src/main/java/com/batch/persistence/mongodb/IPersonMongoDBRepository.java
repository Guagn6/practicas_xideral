package com.batch.persistence.mongodb;

import com.batch.entities.mongodb.PersonMongoDB;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IPersonMongoDBRepository extends MongoRepository<PersonMongoDB, Long> {
    // Buscar por ID de MySQL
    Optional<PersonMongoDB> findByMysqlId(Long mysqlId);

    // Obtener personas migradas por fecha
    @Query("{'migrateAt': {$gte: ?0, $lte: ?1}}")
    List<PersonMongoDB> findPersonsMigratedBetweenDates(String startDate, String endDate);

    // Contar Documentos migrados
    @Query(value = "{}", count = true)
    Long countMigratedPersons();

    // Verificar si existe una persona por ID
    boolean existsByMysqlId(Long mysqlId);
}
