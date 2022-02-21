package com.kpi.tefresolver.controller;

import com.kpi.tefresolver.exception.ReportNotFoundException;
import com.kpi.tefresolver.model.Report;
import com.kpi.tefresolver.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private ReportService reportService;

    @GetMapping
    public List<Report> all() {
        return reportService.getAllRecords();
    }

    @PostMapping
    public Report upload(@RequestParam("file") MultipartFile file) {
        return reportService.saveExcelFile(file);
    }

    @GetMapping("/{id}")
    public Report one(@PathVariable Long id) {
        return reportService.getReportById(id).orElseThrow(() -> new ReportNotFoundException(id));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> download(@PathVariable Long id) {
        List excelFile = reportService.getReportAsExcelFileById(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + excelFile.get(0))
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body((InputStreamResource) excelFile.get(1));
    }

    public ReportService getReportService() {
        return reportService;
    }

    @Autowired
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }
}
