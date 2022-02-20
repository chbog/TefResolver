package com.kpi.tefresolver.controller;

import com.kpi.tefresolver.model.Report;
import com.kpi.tefresolver.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private ReportService reportService;

    @GetMapping
    public List<Report> all(){
        return List.of(new Report()); //stub
    }

    @PostMapping
    public Report upload(@RequestParam("file") MultipartFile file){
        return reportService.saveExcelFile(file);
    }

    @GetMapping("/{id}")
    public Report one(@PathVariable Long id){
        return new Report(); //stub
    }

    public ReportService getReportService() {
        return reportService;
    }

    @Autowired
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }
}
