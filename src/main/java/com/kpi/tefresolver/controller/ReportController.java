package com.kpi.tefresolver.controller;

import com.kpi.tefresolver.exception.ReportNotFoundException;
import com.kpi.tefresolver.model.Report;
import com.kpi.tefresolver.service.InterpolationService;
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
    private InterpolationService interpolationService;

    @Autowired
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public List<Report> all() {
        return reportService.getAllRecords();
    }

    @PostMapping
    public Report upload(@RequestParam("file") MultipartFile file) {
        return reportService.saveExcelFile(file);
    }

    @PutMapping("/{id}/interpolate")
    public Report interpolate(@PathVariable Long id, @RequestParam("method") String method){
        return interpolationService.interpolateReport(id, method);
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

    public InterpolationService getInterpolationService() {
        return interpolationService;
    }

    @Autowired
    public void setInterpolationService(InterpolationService interpolationService) {
        this.interpolationService = interpolationService;
    }
}
