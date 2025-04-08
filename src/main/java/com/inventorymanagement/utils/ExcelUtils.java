package com.inventorymanagement.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
public class ExcelUtils {
    public static CellStyle setCellStyle(
            Workbook workbook,
            short fontColor,
            boolean isBold,
            boolean isItalic,
            double fontHeight,
            short foreColor) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(fontColor);
        font.setBold(isBold);
        font.setItalic(isItalic);
        font.setFontHeightInPoints((short) fontHeight);  // Ensure font height is set
        style.setFont(font);

        // Setting borders
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        style.setWrapText(true);
        style.setFillForegroundColor(foreColor);
        style.setFillBackgroundColor(foreColor);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setQuotePrefixed(true);

        // Alignments
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }
    public static void createCell(Row row, int columnCount, Object data, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        try {
            if (data == null || StringUtils.isEmpty(data.toString())) {
                cell.setCellValue(StringUtils.EMPTY);
                cell.setCellStyle(style);
                return;
            }
            if (data instanceof String string) {
                cell.setCellValue(string);
            } else if (data instanceof Date date) {
                cell.setCellValue(date);
            } else if (data instanceof Long l) {
                cell.setCellValue(l);
            } else if (data instanceof Integer i) {
                cell.setCellValue(i);
            } else if (data instanceof Double d) {
                cell.setCellValue(d);
            } else if (data instanceof Float f) {
                cell.setCellValue(f);
            } else if (data instanceof Short s) {
                cell.setCellValue(s);
            } else if (data instanceof Boolean b) {
                cell.setCellValue(b);
            } else if (data instanceof BigDecimal bd) {
                cell.setCellValue(bd.doubleValue());
            }
        } catch (Exception ex) {
            cell.setCellValue(StringUtils.EMPTY);
        }
        cell.setCellStyle(style);
    }
    public static CellStyle setCellStyle(
            Workbook workbook, short fontColor, boolean isBold, boolean isItalic, double fontHeight, VerticalAlignment verticalAlignment, HorizontalAlignment horizontalAlignment) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(fontColor);
        font.setBold(isBold);
        font.setItalic(isItalic);
        style.setFont(font);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(horizontalAlignment);
        style.setVerticalAlignment(verticalAlignment);
        style.setWrapText(true);
        style.setQuotePrefixed(true);
        return style;
    }
    public static void applyBordersToMergedCells(Sheet sheet) {
        // Loop over all merged regions in the sheet
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);
            int firstRow = mergedRegion.getFirstRow();
            int lastRow = mergedRegion.getLastRow();
            int firstCol = mergedRegion.getFirstColumn();
            int lastCol = mergedRegion.getLastColumn();

            // Apply borders to all cells within the merged region
            for (int row = firstRow; row <= lastRow; row++) {
                for (int col = firstCol; col <= lastCol; col++) {
                    Row rowObj = sheet.getRow(row);
                    if (rowObj == null) rowObj = sheet.createRow(row);
                    Cell cell = rowObj.getCell(col);
                    if (cell == null) cell = rowObj.createCell(col);
                    cell.setCellStyle(sheet.getRow(firstRow).getCell(firstCol).getCellStyle());
                }
            }
        }
    }
}
