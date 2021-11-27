package com.lion.upms.utils;

import com.lion.exception.BusinessException;
import com.lion.utils.MessageI18nUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ImportExcelUtil {

    public static void check(Row row, List<String> listRowKey) {
        if (Objects.isNull(row)) {
            BusinessException.throwException("excel row not empty");
        }
        if (Objects.isNull(listRowKey) || listRowKey.size()<0) {
            BusinessException.throwException("listRowKey not empty");
        }
        for (int i = 0; i<listRowKey.size()-1; i++) {
            if (!getCellValue(row.getCell(i)).toString().equals(String.valueOf(listRowKey.get(i)))) {
                BusinessException.throwException(MessageI18nUtil.getMessage("0000032",new Object[]{(i+1),String.valueOf(listRowKey.get(i))}));
            }
        }
    }

    public static Object getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        Object object = "";
        switch (cell.getCellType()) {
            case STRING:
                object = cell.getStringCellValue();
                break;
            case NUMERIC:
                object = cell.getNumericCellValue();
                break;
            case BOOLEAN:
                object = cell.getBooleanCellValue();
                break;
            default:
                break;
        }
        return object;
    }
    public static boolean isExcel2003(String filePath) {
        return StringUtils.hasText(filePath) && filePath.endsWith(".xls");
    }

    public static boolean isExcel2007(String filePath) {
        return StringUtils.hasText(filePath) && filePath.endsWith(".xlsx");
    }

    public static Optional<Workbook> getWorkbook(InputStream inputStream, String fileName) throws IOException {
        Workbook wookbook = null;
        if (isExcel2003(fileName)) {
            wookbook = new HSSFWorkbook(inputStream);
        } else if (isExcel2007(fileName)) {
            wookbook = new XSSFWorkbook(inputStream);
        }
        return Optional.of(wookbook);
    }
}
