package com.orlando.practicaxideral.crud.controllers;

import com.orlando.practicaxideral.crud.models.Cuenta;
import com.orlando.practicaxideral.crud.services.ClienteService;
import com.orlando.practicaxideral.crud.services.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/banca/cuentas")
@CrossOrigin(origins = "*")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @PostMapping
    public ResponseEntity<Cuenta> createCuenta(@RequestBody Cuenta cuenta) {
        try {
            if (cuentaService.existsCuentaById(cuenta.getidCuenta())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            Cuenta savedCuenta = cuentaService.save(cuenta);
//            clienteService.updateCuenta(savedCuenta.getIdCliente(), savedCuenta); TODO falta guardar en cliente
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCuenta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Cuenta>> getAllCuentas() {
        try {
            List<Cuenta> clientes = cuentaService.findAllCuentas();
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cuenta> getCuentaById(@PathVariable String id) {
        try {
            Optional<Cuenta> cuenta = cuentaService.findById(id);
            return cuenta.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cuenta> updateCuenta(@PathVariable String id, @RequestBody Cuenta cuenta) {
        try {
            Cuenta updatedCuenta = cuentaService.update(id, cuenta);
            return ResponseEntity.ok(updatedCuenta);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCuenta(@PathVariable String id) {
        try {
            if (cuentaService.existsCuentaById(id)) {
                cuentaService.deleteCuentaById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}