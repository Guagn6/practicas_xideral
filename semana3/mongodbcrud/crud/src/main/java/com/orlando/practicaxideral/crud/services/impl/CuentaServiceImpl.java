package com.orlando.practicaxideral.crud.services.impl;

import com.orlando.practicaxideral.crud.models.Cliente;
import com.orlando.practicaxideral.crud.models.Cuenta;
import com.orlando.practicaxideral.crud.repositories.ClienteRepository;
import com.orlando.practicaxideral.crud.repositories.CuentaRepository;
import com.orlando.practicaxideral.crud.services.ClienteService;
import com.orlando.practicaxideral.crud.services.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CuentaServiceImpl implements CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Override
    public Cuenta save(Cuenta cuenta) {
            return cuentaRepository.save(cuenta);
        }

    @Override
    public List<Cuenta> findAllCuentas() {
        return cuentaRepository.findAll();
    }

    @Override
    public Optional<Cuenta> findById(String idCuenta) {
        return cuentaRepository.findById(idCuenta);
    }

    @Override
    public Cuenta update(String id, Cuenta cuenta) {
        Optional<Cuenta> auxCuenta = cuentaRepository.findById(id);
        if (auxCuenta.isPresent()) {
            Cuenta cuentaActualizada = auxCuenta.get();
            cuentaActualizada.setNombre(cuenta.getNombre());
            cuentaActualizada.setSaldo(cuenta.getSaldo());
            return cuentaActualizada;
        }
        throw new RuntimeException("Cliente con ID" + id + "no encontrado");
    }


    @Override
    public void deleteCuentaById(String id) {
        cuentaRepository.deleteById(id);
    }

    @Override
    public boolean existsCuentaById(String id) {
        return cuentaRepository.existsById(id);
    }
}
