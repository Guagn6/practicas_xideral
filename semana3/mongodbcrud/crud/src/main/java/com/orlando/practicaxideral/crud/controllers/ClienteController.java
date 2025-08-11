package com.orlando.practicaxideral.crud.controllers;

import com.orlando.practicaxideral.crud.models.Cliente;
import com.orlando.practicaxideral.crud.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/banca/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<Cliente> createCliente(@RequestBody Cliente cliente) {
        try {
            if (clienteService.existsClienteByEmail(cliente.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            Cliente savedCliente = clienteService.save(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCliente);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> getAllClientes() {
        try {
            List<Cliente> clientes = clienteService.findAllClientes();
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable String id) {
        try {
            Optional<Cliente> cliente = clienteService.findClienteById(id);
            return cliente.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Cliente> getClienteByEmail(@PathVariable String email) {
        try {
            Optional<Cliente> cliente = clienteService.findClienteByEmail(email);
            return cliente.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Cliente>> searchClientesByNombre(@RequestParam String nombre) {
        try {
            List<Cliente> clientes = clienteService.findClientesByNombre(nombre);
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable String id, @RequestBody Cliente cliente) {
        try {
            Cliente updatedCliente = clienteService.update(id, cliente);
            return ResponseEntity.ok(updatedCliente);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable String id) {
        try {
            if (clienteService.findClienteById(id).isPresent()) {
                clienteService.deleteClienteById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/email/{email}")
    public ResponseEntity<Void> deleteClienteByEmail(@PathVariable String email) {
        try {
            if (clienteService.existsClienteByEmail(email)) {
                clienteService.deleteClienteByEmail(email);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}