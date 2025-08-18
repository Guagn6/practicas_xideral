package com.batch.persistence.mysql;

import com.batch.entities.mysql.PersonMySQL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IPersonMySQLRepository extends JpaRepository<PersonMySQL, Long> {
    // Obtener personas activas
    @Query("SELECT p FROM PersonMySQL p WHERE p.status = 'ACTIVE'")
    List<PersonMySQL> findActivePersons();

    // Obtener personas por rango de ID para procesamiento en Chunks
    @Query("SELECT p FROM PersonMySQL p WHERE p.id BETWEEN :startId AND :endId AND p.status = 'ACTIVE'")
    List<PersonMySQL> findPersonByIdRange(Long startId, Long endId);

    // Contar personas activas
    @Query("SELECT COUNT(p) FROM PersonMySQL p WHERE p.status = 'ACTIVE'")
    Long countActivePersons();
}
