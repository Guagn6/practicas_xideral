package com.javatechie.spring.batch.config;

import com.javatechie.spring.batch.entity.ProcessingReport;
import org.springframework.batch.item.ItemWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportItemWriter implements ItemWriter<ProcessingReport> {
    
    @Override
    public void write(List<? extends ProcessingReport> reports) throws Exception {
        if (reports.isEmpty()) {
            return;
        }
        
        ProcessingReport report = reports.get(0);
        String fileName = "processing-report-" + 
                report.getReportDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) + 
                ".txt";
        
        try (FileWriter writer = new FileWriter("reports/" + fileName)) {
            writer.write(report.toReportString());
            System.out.println("Report generated: reports/" + fileName);
        } catch (IOException e) {
            try (FileWriter writer = new FileWriter(fileName)) {
                writer.write(report.toReportString());
                System.out.println("Report generated: " + fileName);
            }
        }
    }
}