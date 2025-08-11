package com.orlando.practicaxideral.crud.services;

import com.orlando.practicaxideral.crud.models.Cliente;
import com.orlando.practicaxideral.crud.models.Cuenta;

import java.util.List;
import java.util.Optional;

public interface CuentaService {
    Cuenta save(Cuenta cuenta);
    List<Cuenta> findAllCuentas();
    Optional<Cuenta> findById(String idCuenta);
    Cuenta update(String id, Cuenta cuenta);
    void deleteCuentaById(String id);
    boolean existsCuentaById(String id);
}


