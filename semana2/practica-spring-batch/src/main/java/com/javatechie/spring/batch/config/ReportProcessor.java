package com.javatechie.spring.batch.config;

import com.javatechie.spring.batch.entity.ProcessingReport;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;

public class ReportProcessor implements ItemProcessor<ProcessingReport, ProcessingReport> {
    
    private final long startTime;
    
    public ReportProcessor(long startTime) {
        this.startTime = startTime;
    }
    
    @Override
    public ProcessingReport process(ProcessingReport report) throws Exception {
        long endTime = System.currentTimeMillis();
        report.setProcessingTimeMs(endTime - startTime);
        
        long filteredRecords = report.getRecordsByCountry().getOrDefault("China", 0L);
        report.setTotalRecordsFiltered(filteredRecords);
        
        report.setReportDate(LocalDateTime.now());
        
        return report;
    }
}