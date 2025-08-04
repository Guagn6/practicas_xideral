package com.javatechie.spring.batch.config;

import com.javatechie.spring.batch.entity.Student;
import org.springframework.batch.item.ItemProcessor;

public class StudentProcessor implements ItemProcessor<Student, Student> {

    @Override
    public Student process(Student student) throws Exception {
        if(student.getCountry().equals("China") && student.getGender().equals("Female")) {
            return student;
        }else{
            return null;
        }
    }
}
