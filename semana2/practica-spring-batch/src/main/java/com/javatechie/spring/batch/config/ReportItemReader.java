package com.javatechie.spring.batch.config;

import com.javatechie.spring.batch.entity.Student;
import com.javatechie.spring.batch.entity.ProcessingReport;
import com.javatechie.spring.batch.repository.StudentRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportItemReader implements ItemReader<ProcessingReport> {
    private StudentRepository studentRepository;
    private boolean hasRead = false;
    
    public ReportItemReader(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    
    @Override
    public ProcessingReport read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (hasRead) {
            return null;
        }
        
        hasRead = true;
        
        List<Student> students = studentRepository.findAll();
        
        ProcessingReport report = new ProcessingReport();
        report.setReportDate(LocalDateTime.now());
        report.setTotalRecordsProcessed(students.size());
        report.setJobStatus("COMPLETED");
        
        Map<String, Long> countryStats = students.stream()
                .collect(Collectors.groupingBy(Student::getCountry, Collectors.counting()));
        
        Map<String, Long> genderStats = students.stream()
                .collect(Collectors.groupingBy(Student::getGender, Collectors.counting()));
        
        report.setRecordsByCountry(countryStats);
        report.setRecordsByGender(genderStats);
        
        return report;
    }
}