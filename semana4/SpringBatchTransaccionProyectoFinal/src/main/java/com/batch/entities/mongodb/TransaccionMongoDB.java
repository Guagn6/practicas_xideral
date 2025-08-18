package com.batch.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.*;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transacciones")
public class TransaccionMongoDB {

    @Id
    private String id;

    @Field("mysql_id")
    private Long mysqlId;

    @Field("name")
    private String name;

    @Field("cuenta_origen")
    private String cuentaOrigen;

    @Field("cuenta_destino")
    private String cuentaDestino;

    @Field("monto")
    private Double monto;

    @Field("moneda")
    private String moneda;

    @Field("fecha")
    private Date fecha;

    @Field("estado")
    private String estado;

    @Field("tipo")
    private String tipo;

    @Field("referencia")
    private String referencia;

    @Field("descripcion")
    private String descripcion;

    @Field("sucursal")
    private String sucursal;

    @Field("migrated_at")
    private LocalDateTime migratedAt;

    @Field("migration_source")
    private String migrationSource = "MYSQL_BATCH";

    @Field("procesado")
    private Boolean procesado = false;
}
