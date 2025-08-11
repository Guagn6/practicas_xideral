package com.orlando.practicaxideral.crud.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "clientes")
public class Cliente {

    @Id
    private String id;
    private String nombre;

    @Indexed(unique = true)
    private String email;
    private String telefono;
    private LocalDateTime fechaRegistro;
    private Cuenta cuenta;

    public Cliente() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public Cliente(String nombre, String email, String telefono, LocalDateTime fechaRegistro) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.fechaRegistro = fechaRegistro;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public void transferir(Cuenta origen, Cuenta destino, BigDecimal monto) {
        origen.pasivo(monto);
        destino.activo(monto);
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }
}
