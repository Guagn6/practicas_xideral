package com.batch.entities.mongodb;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "persons")
public class PersonMongoDB {
    @Id
    private String id;

    @Field("mysql_id")
    private Long mysqlId;

    @Field("name")
    private String name;

    @Field("last_name")
    private String lastName;

    @Field("age")
    private String age;

    @Field("create_at")
    private LocalDateTime createAt;

    @Field("migrated_at")
    private LocalDateTime migratedAt;

    @Field("status")
    private String status;

    @Field("migration_source")
    private String migrationSource = "MYSQL_BATCH";
}
