package com.kpi.tefresolver.service;

import com.kpi.tefresolver.model.Report;
import com.kpi.tefresolver.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ReportService {
    private ReportRepository reportRepository;

//    public Report saveExcelFile(MultipartFile file){
//        file.con
//    }


    public ReportRepository getReportRepository() {
        return reportRepository;
    }

    @Autowired
    public void setReportRepository(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }
}
