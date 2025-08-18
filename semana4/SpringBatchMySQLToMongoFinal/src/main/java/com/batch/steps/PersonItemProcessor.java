package com.batch.steps;

import com.batch.entities.mongodb.PersonMongoDB;
import com.batch.entities.mysql.PersonMySQL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class PersonItemProcessor implements ItemProcessor<PersonMySQL, PersonMongoDB> {

    @Override
    public PersonMongoDB process(PersonMySQL personMySQL) throws Exception {
        log.debug("Procesando persona desde MySQL: ID={}, Nombre={} {}",
                personMySQL.getId(), personMySQL.getName(), personMySQL.getLastName());

        // Validaciones de datos
        if (personMySQL.getName() == null || personMySQL.getName().trim().isEmpty()) {
            log.warn("Persona con ID {} tiene nombre vacío, se omitirá", personMySQL.getId());
            return null; // Si retorna null, el item se omite
        }

        // Crear el documento MongoDB
        PersonMongoDB personMongoDB = new PersonMongoDB();

        // Mapeo de campos
        personMongoDB.setMysqlId(personMySQL.getId());
        personMongoDB.setName(cleanAndFormat(personMySQL.getName()));
        personMongoDB.setLastName(cleanAndFormat(personMySQL.getLastName()));
        personMongoDB.setAge(String.valueOf(personMySQL.getAge()));
        personMongoDB.setCreateAt(personMySQL.getCreateAt());
        personMongoDB.setMigratedAt(LocalDateTime.now());
        personMongoDB.setStatus(personMySQL.getStatus());
        personMongoDB.setMigrationSource("MYSQL_BATCH");

        log.debug("Persona transformada para MongoDB: mysqlId={}, nombre={} {}",
                personMongoDB.getMysqlId(), personMongoDB.getName(), personMongoDB.getLastName());

        return personMongoDB;
    }

    // Limpia y formatea strings
    private String cleanAndFormat(String input) {
        if (input == null) {
            return null;
        }
        return input.trim().toUpperCase();
    }
}
