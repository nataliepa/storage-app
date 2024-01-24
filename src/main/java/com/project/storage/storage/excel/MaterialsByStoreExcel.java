
package com.project.storage.storage.excel;

import com.project.storage.storage.entity.Orders;
import com.project.storage.storage.entity.Store;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.Date;
import java.util.List;

// κλαση που εξαγει τα αποθεματα δευτερευοντων αποθήκων  σε excel
public class MaterialsByStoreExcel {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Orders> orders;
    private  Store store;


    public MaterialsByStoreExcel(List<Orders> orders, Store store) {
        this.orders = orders;
        this.store = store;
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

        CellStyle styleStore = workbook.createCellStyle();
        XSSFFont fontBlue = workbook.createFont();
        fontBlue.setFontHeight(16);
        fontBlue.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
        styleStore.setFont(fontBlue);

        int countColumn = 0;


        //header
        createCell(row, countColumn++,store.getTitle() , styleStore);
        createCell(row, countColumn++, "Κωδικός Παραγγελίας", style);
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
        for (Orders ord : orders) {
            foundPr = found(ord);
            Row row = sheet.createRow(rowCount++);
            int columnCount = 1;
            createCell(row, columnCount++, ord.getId() == null ? "" : ord.getId().toString(), foundPr ? styleRed : style);

            createCell(row, columnCount++, ord.getMaterialByMaterialId() == null ? "" : ord.getMaterialByMaterialId().getId().toString(), foundPr ? styleRed : style);
            createCell(row, columnCount++, " ", foundPr ? styleRed : style);

            createCell(row, columnCount++, ord.getMaterialByMaterialId() == null ? "" : ord.getMaterialByMaterialId().getText(), foundPr ? styleRed : style);

            createCell(row, columnCount++, ord.getMaterialByMaterialId() == null ? "" : ord.getMaterialByMaterialId().getMsizeByMsize().getName(), foundPr ? styleRed : style);

            createCell(row, columnCount++, ord.getStock() == null ? "" : ord.getStock().toString(), foundPr ? styleRed : style);
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


    public boolean found(Orders orders) {

        if (orders.getStock() == 0) {
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
