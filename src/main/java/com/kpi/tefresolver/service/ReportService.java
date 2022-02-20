package com.kpi.tefresolver.service;

import com.kpi.tefresolver.model.Report;
import com.kpi.tefresolver.repository.ReportRepository;
import com.kpi.tefresolver.util.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ReportService {
    private ReportRepository reportRepository;

    public Report saveExcelFile(MultipartFile file){
        if(ExcelUtil.isExcelFormat(file)){
            return ExcelUtil.excelToReport(file);
        }
        throw new IllegalArgumentException("file is not of excel format");
    }


    public ReportRepository getReportRepository() {
        return reportRepository;
    }

    @Autowired
    public void setReportRepository(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }
}
