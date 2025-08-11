package com.orlando.practicaxideral.crud.services.impl;

import com.orlando.practicaxideral.crud.models.Cliente;
import com.orlando.practicaxideral.crud.models.Cuenta;
import com.orlando.practicaxideral.crud.repositories.ClienteRepository;
import com.orlando.practicaxideral.crud.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public Cliente save(Cliente cliente) {
        if (cliente.getId() == null) {
            cliente.setFechaRegistro(LocalDateTime.now());
        }
        return clienteRepository.save(cliente);
    }

    @Override
    public List<Cliente> findAllClientes() {
        return clienteRepository.findAll();
    }

    @Override
    public Optional<Cliente> findClienteById(String id) {
        return clienteRepository.findById(id);
    }

    @Override
    public List<Cliente> findClientesByNombre(String nombre) {
        return clienteRepository.findByNombre(nombre);
    }

    @Override
    public Optional<Cliente> findClienteByEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    @Override
    public Cliente update(String id, Cliente cliente) {
        Optional<Cliente> auxCliente = clienteRepository.findById(id);
        if (auxCliente.isPresent()) {
            Cliente clienteActualizado = auxCliente.get();
            clienteActualizado.setNombre(cliente.getNombre());
            clienteActualizado.setEmail(cliente.getEmail());
            clienteActualizado.setTelefono(cliente.getTelefono());
            return clienteActualizado;
        }
        throw new RuntimeException("Cliente con ID" + id + "no encontrado");
    }

    @Override
    public Cliente updateCuenta(String id, Cuenta cuenta) {
//        Optional<Cliente> auxCliente = clienteRepository.findById(id);
//        if (auxCliente.isPresent()) {
//            Cliente clienteActualizado = auxCliente.get();
//            clienteActualizado.setCuenta(cuenta); TODO
//            return clienteActualizado;
//        }
//        throw new RuntimeException("Cliente con ID" + id + "no encontrado");
        return null;
    }

    @Override
    public void deleteClienteById(String id)  {
        clienteRepository.deleteById(id);
    }

    @Override
    public void deleteClienteByEmail(String email) {
        clienteRepository.deleteByEmail(email);
    }

    @Override
    public boolean existsClienteByEmail(String email) {
        return clienteRepository.existsByEmail(email);
    }
}
