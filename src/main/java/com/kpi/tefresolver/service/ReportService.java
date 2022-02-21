package com.kpi.tefresolver.service;

import com.kpi.tefresolver.model.ObservationData;
import com.kpi.tefresolver.model.Report;
import com.kpi.tefresolver.repository.ReportRepository;
import com.kpi.tefresolver.util.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;

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

    public ReportRepository getReportRepository() {
        return reportRepository;
    }

    @Autowired
    public void setReportRepository(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }
}
