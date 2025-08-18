package com.batch.steps;

import com.batch.entities.mongodb.PersonMongoDB;
import com.batch.persistence.mongodb.IPersonMongoDBRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class PersonMongoDBItemWriter implements ItemWriter<PersonMongoDB> {

    @Autowired
    private IPersonMongoDBRepository personMongoRepository;

    @Override
    public void write(Chunk<? extends PersonMongoDB> chunk) throws Exception {
        log.info("Escribiendo chunk de {} personas en MongoDB", chunk.size());

        List<PersonMongoDB> personsToSave = new ArrayList<>();
        int duplicateCount = 0;
        int errorCount = 0;

        for (PersonMongoDB person : chunk.getItems()) {
            try {
                // Verificar si ya existe la persona por su ID de MySQL
                if (personMongoRepository.existsByMysqlId(person.getMysqlId())) {
                    log.warn("La persona con mysqlId {} ya existe en MongoDB, se omite",
                            person.getMysqlId());
                    duplicateCount++;
                    continue;
                }

                personsToSave.add(person);
                log.debug("Preparando persona para guardar: mysqlId={}, nombre={} {}",
                        person.getMysqlId(), person.getName(), person.getLastName());

            } catch (Exception e) {
                log.error("Error al verificar persona con mysqlId {}: {}",
                        person.getMysqlId(), e.getMessage());
                errorCount++;
            }
        }

        // Guardar en lote todas las personas válidas
        if (!personsToSave.isEmpty()) {
            try {
                List<PersonMongoDB> savedPersons = personMongoRepository.saveAll(personsToSave);
                log.info("Guardadas exitosamente {} personas en MongoDB", savedPersons.size());

                // Log detallado de cada persona guardada
                savedPersons.forEach(person ->
                        log.debug("Guardada persona: id={}, mysqlId={}, nombre={} {}",
                                person.getId(), person.getMysqlId(),
                                person.getName(), person.getLastName())
                );

            } catch (Exception e) {
                log.error("Error al guardar el lote de personas en MongoDB: {}", e.getMessage());
                throw e; // Re-lanzar para que Spring Batch maneje el error
            }
        }

        // Resumen del procesamiento del chunk
        log.info("Resumen del chunk - Total: {}, Guardadas: {}, Duplicadas: {}, Errores: {}",
                chunk.size(), personsToSave.size(), duplicateCount, errorCount);
    }
}
