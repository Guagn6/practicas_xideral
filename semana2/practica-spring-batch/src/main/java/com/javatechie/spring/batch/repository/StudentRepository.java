package com.javatechie.spring.batch.repository;

import com.javatechie.spring.batch.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student,Integer> {
}
