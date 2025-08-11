package com.orlando.practicaxideral.crud.services;

import com.orlando.practicaxideral.crud.models.Cliente;
import com.orlando.practicaxideral.crud.models.Cuenta;

import java.util.List;
import java.util.Optional;

public interface ClienteService {
    Cliente save(Cliente cliente);
    List<Cliente> findAllClientes();
    List<Cliente> findClientesByNombre(String nombre);
    Optional<Cliente> findClienteById(String id);
    Optional<Cliente> findClienteByEmail(String email);
    Cliente update(String id, Cliente cliente);
    Cliente updateCuenta(String id, Cuenta Cuenta);
    void deleteClienteById(String id);
    void deleteClienteByEmail(String email);
    boolean existsClienteByEmail(String email);
}


