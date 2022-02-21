package com.kpi.tefresolver.util;

import com.google.common.base.Strings;
import com.kpi.tefresolver.model.ObservationData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExcelUtil {
    public static String EXCEL_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

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
