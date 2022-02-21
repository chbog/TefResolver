package com.kpi.tefresolver.service;

import com.kpi.tefresolver.exception.ReportNotFoundException;
import com.kpi.tefresolver.model.ObservationData;
import com.kpi.tefresolver.model.Report;
import com.kpi.tefresolver.repository.ReportRepository;
import com.kpi.tefresolver.util.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {
    private ReportRepository reportRepository;

    public Report saveExcelFile(MultipartFile file){
        if(ExcelUtil.isExcelFormat(file)){
            List<ObservationData> data = ExcelUtil.excelToObservationData(file);
            //must be validated first
            return saveReport(file.getOriginalFilename(), false, data);
        }
        throw new IllegalArgumentException("file is not of excel format");
    }

    @Transactional
    protected Report saveReport(String reportName, boolean valid, List<ObservationData> data){
        Report report = reportRepository.save(new Report(null, reportName, valid, null));
        data.forEach(row -> row.setReport(report));
        report.setRows(data);
        return reportRepository.save(report);
    }

    public List<Report> getAllRecords(){
        return reportRepository.findAll();
    }

    public Optional<Report> getReportById(Long reportId){
        return reportRepository.findById(reportId);
    }

    public List getReportAsExcelFileById(Long id){
        Report report = reportRepository.findById(id).orElseThrow(() -> new ReportNotFoundException(id));
        ByteArrayInputStream excelStream = ExcelUtil.observationDataToExcel(report.getRows(), report.getFileName());
        return List.of(report.getFileName(), new InputStreamResource(excelStream));
    }

    public ReportRepository getReportRepository() {
        return reportRepository;
    }

    @Autowired
    public void setReportRepository(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }
}
