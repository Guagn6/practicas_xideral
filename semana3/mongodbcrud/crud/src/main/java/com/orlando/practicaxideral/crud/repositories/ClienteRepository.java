package com.orlando.practicaxideral.crud.repositories;

import com.orlando.practicaxideral.crud.models.Cliente;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends MongoRepository<Cliente, String> {
    List<Cliente> findByNombre(String nombre);
    Optional<Cliente> findById(String id);
    Optional<Cliente> findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteById(String id);
    void deleteByEmail(String email);
}
