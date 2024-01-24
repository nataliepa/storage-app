package com.project.storage.storage.excel;

import com.project.storage.storage.entity.Material;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.apache.poi.ss.util.CellUtil.createCell;

// κλαση που εξαγει τα αποθεματα κεντρικης αποθήκης σε excel

public class AllMaterialsToExcel {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Material> materials;


    public AllMaterialsToExcel(List<Material> materials) {
        this.materials = materials;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        //set excel properties
        sheet = workbook.createSheet("Ενδύματα");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        int countColumn = 0;
        //header for program
        createCell(row, countColumn++, "Κωδικός προιόντος", style);
        createCell(row, countColumn++, " ", style);
        createCell(row, countColumn++, "Περιγραφή", style);
        createCell(row, countColumn++, "Μέγεθος", style);
        createCell(row, countColumn++, "Απόθεμα", style);


    }


    private void writeDataLines() {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);


        // style red
        CellStyle styleRed = workbook.createCellStyle();
        XSSFFont fontRed = workbook.createFont();
        fontRed.setFontHeight(14);
        fontRed.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        styleRed.setFont(fontRed);


        //write data for each program
        int rowCount = 1;
        int counter = 0;
        boolean foundPr = false;
        for (Material mat : materials) {
            foundPr = found(mat);
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, mat.getId() == null ? "" : mat.getId().toString(), foundPr ? styleRed : style);
            createCell(row, columnCount++, " ", foundPr ? styleRed : style);

            createCell(row, columnCount++, mat.getText() == null ? "" : mat.getText(), foundPr ? styleRed : style);

            createCell(row, columnCount++, mat.getMsizeByMsize() == null ? "" : mat.getMsizeByMsize().getName(), foundPr ? styleRed : style);

            createCell(row, columnCount++, mat.getQuantity() == null ? "" : mat.getQuantity().toString(), foundPr ? styleRed : style);
        }

    }


    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }


    public boolean found(Material material) {

        if (material.getQuantity() == 0) {
            return true;
        } else {
            return false;
        }
    }


    public void export(HttpServletResponse response) throws IOException {

        writeHeaderLine();
        writeDataLines();


        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }


}
