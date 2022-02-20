package com.kpi.tefresolver.util;

import com.google.common.base.Strings;
import com.kpi.tefresolver.model.ObservationData;
import com.kpi.tefresolver.model.Report;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class ExcelUtil {
    public static String EXCEL_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static boolean isExcelFormat(MultipartFile file) {
        return EXCEL_TYPE.equals(file.getContentType());
    }

    public static Report excelToReport(MultipartFile file) {
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<ObservationData> observationDataList = new ArrayList<>();
            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                } else if (Strings.isNullOrEmpty(currentRow.getCell(0).getStringCellValue())) {
                    continue;
                }
                ObservationData observationData = new ObservationData();
                int i = 0;
                observationData.setDayNumber((short) currentRow.getCell(i++).getNumericCellValue());
                String[] hoursAndMinutes = currentRow.getCell(i++).getStringCellValue().split(":");
                observationData.setObservationTime(LocalTime.of(Integer.parseInt(hoursAndMinutes[0]), Integer.parseInt(hoursAndMinutes[1])));
                observationData.setTemperature((short) currentRow.getCell(i++).getNumericCellValue());
                observationData.setWindDirection(currentRow.getCell(i++).getStringCellValue());
                observationData.setWindSpeed((int) currentRow.getCell(i).getNumericCellValue());

                observationDataList.add(observationData);
            }
            workbook.close();
            //needs to be validated first
            return new Report(null, file.getOriginalFilename(), false, observationDataList);
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage()); //needs to be replaced with custom exception and also needs overall refactoring
        }
    }
}
