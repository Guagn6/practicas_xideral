package com.orlando.practicaxideral.crud.models;

import com.orlando.practicaxideral.crud.exceptions.DineroInsuficienteException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "cuentas")
public class Cuenta {

    @Id
    private String idCuenta;
    private String idCliente;
    private String nombre;
    private BigDecimal saldo;

    public Cuenta() {
    }

    public Cuenta(String idCliente, String nombre, BigDecimal saldo) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.saldo = saldo;
    }

    public String getidCuenta() {
        return idCuenta;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }


    public void pasivo(BigDecimal monto) {
        BigDecimal nuevoSaldo = this.saldo.subtract(monto);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new DineroInsuficienteException("Saldo en la cuenta insuficiente.");
        }
        this.saldo = nuevoSaldo;
    }

    public void activo(BigDecimal monto) {
        this.saldo = this.saldo.add(monto);
    }
}