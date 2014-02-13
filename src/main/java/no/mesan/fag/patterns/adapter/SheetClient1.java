package no.mesan.fag.patterns.adapter;


import no.mesan.fag.patterns.timesheet.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.TimeDataServer;
import no.mesan.fag.patterns.timesheet.external.TimeDataService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.LocalDate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A weekly timesheet created using Apache POI.
 */
public class SheetClient1 {
    private static final String[] HEADINGS = { "Person", "Man", "Tir", "Ons", "Tor", "Fre", "Lør", "Søn", "Totalt" };
    private final String forUser;
    private final int year;
    private final int month;
    private final TimeDataService source;

    private enum StyleName {
        H1, TBL_HEAD, COL1, COLN, SUMS
    }

    public static void main(String[] args) throws Exception {
        new SheetClient1("larsr", 2013, 1, new TimeDataServer()).createTimeliste();
    }

    public SheetClient1(final String user, final int year, final int month, final TimeDataService source) {
        super();
        this.forUser = user;
        this.year = year;
        this.month = month;
        this.source = source;
    }

    private void createTimeliste() throws IOException {
        final String sheetTitle = "Timeliste";
        final List<String> tableHeadings= new ArrayList<>();
        tableHeadings.add("Aktivitet");
        for (int i=1; i<=31 ; i++) tableHeadings.add(String.format("%2d", i));
        tableHeadings.add("SUM");

        // Hent timedata for bruker
        final List<TimesheetEntry> fullList= this.source.forEmployee(this.forUser);
        // Filtrer for aktuelt tidsrom
        final List<TimesheetEntry> list= new ArrayList<>();
        for (TimesheetEntry entry : fullList) {
            final LocalDate when = entry.getWhen();
            if (when.year().get()==this.year && when.monthOfYear().get()==this.month) list.add(entry);
        }
        // Grupper data
        final Map<Integer, double[]> lines= new HashMap<>();
        for (TimesheetEntry entry : list) {
            final int what = entry.getActivity();
            ensureRow(lines, what);
            final double[] line = lines.get(what);
            final double hours= (entry.getMinutes()/30) / 2.0;
            final int day= entry.getWhen().getDayOfMonth();
            line[day-1] += hours;
        }

        // Lag en arbeidsbok med 1 side
        final Workbook workbook= new XSSFWorkbook();
        final Sheet sheet = workbook.createSheet(sheetTitle);
        pageSetup(sheet);

        // Lag nødvendige stiler
        final Map<StyleName, CellStyle> styles = styleSetup(workbook);

        // Hovedoverskrift
        int rownum = 0;
        final Row heading1= createRow(sheet, 0, 45);
        Cell heading1cell = heading1.createCell(rownum++);
        heading1cell.setCellValue("Timeliste");
        heading1cell.setCellStyle(styles.get(StyleName.H1));

        // Tabelloverskrift
        rownum++; // Hopp over en linje
        final Row tableHead = createRow(sheet, rownum++, 40);
        int col= 0;
        for (String header : tableHeadings) {
            Cell headCell = tableHead.createCell(col++);
            headCell.setCellValue(header); // TODO asString!
            headCell.setCellStyle(styles.get(StyleName.TBL_HEAD));
        }

        // Datalinjer
        for (Map.Entry<Integer, double[]> entry : lines.entrySet()) {
            col= 0;
            final Row row = createRow(sheet, rownum++, -1);
            final Cell cell1 = row.createCell(col++);
            cell1.setCellValue(entry.getKey());
            cell1.setCellStyle(styles.get(StyleName.COL1));
            final double[] values = entry.getValue();
            for (int i = 0; i < values.length; i++) {
                row.createCell(col++).setCellValue(values[i]);
            }
            // Sum til slutt
            final Cell cellN = row.createCell(col++);
            final String ref = "B" +rownum+ ":" + /*(char) ('B' + values.length)  TODO*/ "AF" + rownum;
            cellN.setCellFormula("SUM("+ref+")");
            cellN.setCellStyle(styles.get(StyleName.COLN));
        }

        // Sumlinje
        col= 0;
        final Row row = createRow(sheet, rownum++, -1);
        final Cell cell1 = row.createCell(col++);
        cell1.setCellValue("SUM");
        cell1.setCellStyle(styles.get(StyleName.SUMS));
        for (int i = 0; i < 31+1; i++) {
            final Cell cell = row.createCell(col++);
            final char rowLetter = (char) ('B' + i);
            if (rowLetter<='Z') {   ///////////////// FIXME!!!!!!!!!!!!!!!!!
            final String ref = rowLetter + "4:" + rowLetter + (rownum-1);
            System.out.println(ref);
            cell.setCellFormula("SUM(" + ref + ")");
            }
            cell.setCellStyle(styles.get(StyleName.SUMS));
        }

        //finally set column widths, the width is measured in units of 1/256th of a character width
        sheet.setColumnWidth(0, 25*256); //25 characters wide
        for (int i = 1; i <= 31; i++) {
            sheet.setColumnWidth(i, 5*256);  //5 characters wide
        }
        sheet.setColumnWidth(32, 10*256); //10 characters wide

        // Write the output to a file
        final String fileName = sheetTitle + ".xlsx";
        FileOutputStream out = new FileOutputStream(fileName);
        workbook.write(out);
        out.close();
    }

    private void ensureRow(final Map<Integer, double[]> lines, final int what) {
        if (!lines.containsKey(what)){
            final double[] sums = new double[31];
            lines.put(what, sums);
        }
    }

    private Row createRow(final Sheet sheet, final int rownum, final int height) {
        final Row heading1;
        heading1= sheet.createRow(rownum);
        if (height>0) heading1.setHeightInPoints(height);
        return heading1;
    }

    private void pageSetup(final Sheet sheet) {PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);
    }

    /**
     * Create a library of cell styles
     */
    private static Map<StyleName, CellStyle> styleSetup(Workbook wb){
        Map<StyleName, CellStyle> styles = new HashMap<>();
        styles.put(StyleName.H1, createStyle(wb, 18, Font.BOLDWEIGHT_BOLD, IndexedColors.DARK_BLUE, null,
                                             CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER));
        styles.put(StyleName.TBL_HEAD, createStyle(wb, 15, Font.BOLDWEIGHT_BOLD, IndexedColors.DARK_BLUE,
                                                   IndexedColors.WHITE, CellStyle.ALIGN_RIGHT,
                                                   CellStyle.VERTICAL_CENTER));
        styles.put(StyleName.COL1, createStyle(wb, 10, Font.BOLDWEIGHT_BOLD, IndexedColors.AUTOMATIC, null,
                                               CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_BOTTOM));
        styles.put(StyleName.COLN, createStyle(wb, 10, Font.BOLDWEIGHT_BOLD, IndexedColors.AUTOMATIC, null,
                                               CellStyle.ALIGN_RIGHT, CellStyle.VERTICAL_BOTTOM));
        final CellStyle style = createStyle(wb, 12, Font.BOLDWEIGHT_BOLD, IndexedColors.WHITE,
                                            IndexedColors.DARK_BLUE, CellStyle.ALIGN_GENERAL,
                                            CellStyle.VERTICAL_BOTTOM);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        styles.put(StyleName.SUMS, style);

//        Font monthFont = wb.createFont();
//        monthFont.setFontHeightInPoints((short)11);
//        monthFont.setColor(IndexedColors.WHITE.getIndex());
//        style = wb.createCellStyle();
//        style.setAlignment(CellStyle.ALIGN_CENTER);
//        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
//        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
//        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
//        style.setFont(monthFont);
//        style.setWrapText(true);
//        styles.put("header", style);
//
//        style = wb.createCellStyle();
//        style.setAlignment(CellStyle.ALIGN_CENTER);
//        style.setWrapText(true);
//        style.setBorderRight(CellStyle.BORDER_THIN);
//        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
//        style.setBorderLeft(CellStyle.BORDER_THIN);
//        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
//        styles.put("cell", style);
//
//        style = wb.createCellStyle();
//        style.setAlignment(CellStyle.ALIGN_CENTER);
//        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
//        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
//        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
//        style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
//        styles.put("formula", style);
//
//        style = wb.createCellStyle();
//        style.setAlignment(CellStyle.ALIGN_CENTER);
//        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
//        style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
//        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
//        style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
//        styles.put("formula_2", style);

        return styles;
    }

    private static CellStyle createStyle(final Workbook wb, final int pointSize, final short fontWeight,
                                         final IndexedColors color, final IndexedColors fillColor,
                                         final short horAlign, final short vertAlign) {
        final CellStyle style=  wb.createCellStyle();
        final Font font = wb.createFont();
        font.setFontHeightInPoints((short) pointSize);
        font.setColor(color.getIndex());
        font.setBoldweight(fontWeight);
        style.setFont(font);
        if (fillColor!=null) {
            style.setFillForegroundColor(fillColor.getIndex());
            style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        }
        style.setAlignment(horAlign);
        style.setVerticalAlignment(vertAlign);
        return style;
    }
}

// License from Apache POI samples by Yegor Kozlov, from which this class has lent a lot
/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
