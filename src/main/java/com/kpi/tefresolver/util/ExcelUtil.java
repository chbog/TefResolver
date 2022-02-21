package com.kpi.tefresolver.util;

import com.google.common.base.Strings;
import com.kpi.tefresolver.model.ObservationData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component
public class ExcelUtil {
    public static String EXCEL_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static String[] HEADERS = {"Month number", "UTC", "Temperature, °C", "Wind direction", "Average wind speed, m/s"};

    public static ByteArrayInputStream observationDataToExcel(List<ObservationData> observationDataList, String sheetName) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(sheetName);
            // Header
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < HEADERS.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERS[col]);
            }
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            CellStyle style = workbook.createCellStyle();
            DataFormat df = workbook.createDataFormat();
            style.setDataFormat(df.getFormat("hh:mm"));

            int rowIdx = 1;
            for (ObservationData data : observationDataList) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0, CellType.NUMERIC).setCellValue(data.getDayNumber());
                Cell observationTimeCell = row.createCell(1, CellType.FORMULA);
                observationTimeCell.setCellFormula("TIME(" + data.getObservationTime().getHour()
                        + ", " + data.getObservationTime().getMinute() + ", 00)");
                observationTimeCell.setCellStyle(style);
//                row.createCell(1, CellType.NUMERIC).setCellValue();
//                row.createCell(2, CellType.NUMERIC).setCellValue(tutorial.getDescription());
//                row.createCell(3).setCellValue(tutorial.isPublished());
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }

    public static boolean isExcelFormat(MultipartFile file) {
        return EXCEL_TYPE.equals(file.getContentType());
    }

    public static List<ObservationData> excelToObservationData(MultipartFile file) {
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            List<ObservationData> observationDataList = new ArrayList<>();
            boolean zeroRow = true;
            for (Row currentRow : sheet) {
                // skip header
                if (zeroRow) {
                    zeroRow = false;
                    continue;
                }
                ObservationData observationData = new ObservationData();
                for (Cell currentCell : currentRow) {
                    extractAndSetCellValue(observationData, currentCell);
                }
                observationDataList.add(observationData);
            }
            workbook.close();
            //needs to be validated first
            return observationDataList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage()); //needs to be replaced with custom exception and also needs overall refactoring
        }
    }

    private static void extractAndSetCellValue(ObservationData data, Cell cell) {
        try {
            switch (cell.getColumnIndex()) {
                case 0:
                    extractAndSetDayNumber(data, cell);
                    break;
                case 1:
                    extractAndSetObservationTime(data, cell);
                    break;
                case 2:
                    extractAndSetTemperature(data, cell);
                    break;
                case 3:
                    extractAndSetWindDirection(data, cell);
                    break;
                case 4:
                    extractAndSetWindSpeed(data, cell);
                    break;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void extractAndSetDayNumber(ObservationData data, Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            data.setDayNumber((short) cell.getNumericCellValue());
        }
    }

    private static void extractAndSetObservationTime(ObservationData data, Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            data.setObservationTime(cell.getLocalDateTimeCellValue().toLocalTime());
        }
    }

    private static void extractAndSetTemperature(ObservationData data, Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            data.setTemperature((short) cell.getNumericCellValue());
        }
    }

    private static void extractAndSetWindDirection(ObservationData data, Cell cell) {
        if (cell.getCellType() == CellType.STRING) {
            String windDirection = cell.getStringCellValue();
            if (!Strings.isNullOrEmpty(windDirection)) {
                data.setWindDirection(windDirection);
            }
        }
    }

    private static void extractAndSetWindSpeed(ObservationData data, Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            data.setWindSpeed((int) cell.getNumericCellValue());
        }
    }

}
