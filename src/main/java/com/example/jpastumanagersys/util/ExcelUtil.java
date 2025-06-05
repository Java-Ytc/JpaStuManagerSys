package com.example.jpastumanagersys.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ExcelUtil {
    public ByteArrayInputStream exportToExcel(List<Map<String, Object>> data, String[] headers) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        // 创建表头
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 填充数据
        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.createRow(i + 1);
            Map<String, Object> rowData = data.get(i);
            for (int j = 0; j < headers.length; j++) {
                Cell cell = row.createCell(j);
                Object value = rowData.get(headers[j]);
                if (value != null) {
                    cell.setCellValue(value.toString());
                }
            }
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 输出到流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}
