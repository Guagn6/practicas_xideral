package com.javatechie.spring.batch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessingReport {
    
    private LocalDateTime reportDate;
    private long totalRecordsProcessed;
    private long totalRecordsFiltered;
    private Map<String, Long> recordsByCountry;
    private Map<String, Long> recordsByGender;
    private String jobStatus;
    private long processingTimeMs;
    
    public String toReportString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== STUDENT PROCESSING REPORT ===\n");
        sb.append("Report Date: ").append(reportDate).append("\n");
        sb.append("Job Status: ").append(jobStatus).append("\n");
        sb.append("Processing Time: ").append(processingTimeMs).append(" ms\n");
        sb.append("Total Records Processed: ").append(totalRecordsProcessed).append("\n");
        sb.append("Total Records Filtered: ").append(totalRecordsFiltered).append("\n");
        sb.append("\n--- STATISTICS BY COUNTRY ---\n");
        recordsByCountry.forEach((country, count) -> 
            sb.append(country).append(": ").append(count).append("\n"));
        sb.append("\n--- STATISTICS BY GENDER ---\n");
        recordsByGender.forEach((gender, count) -> 
            sb.append(gender).append(": ").append(count).append("\n"));
        sb.append("===================================\n");
        return sb.toString();
    }
}