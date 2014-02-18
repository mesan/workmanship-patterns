package no.mesan.fag.patterns.timesheet;

import no.mesan.fag.patterns.timesheet.data.DoubleMatrix;
import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.TimeDataServer;
import no.mesan.fag.patterns.timesheet.external.TimeDataService;

import no.mesan.fag.patterns.timesheet.format.StyleFactory;
import no.mesan.fag.patterns.timesheet.format.StyleName;
import no.mesan.fag.patterns.timesheet.utils.SheetUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.joda.time.LocalDate;

import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Timeliste for en enkelt bruker for en måned.
 */
public class Timeliste {
    private final String forUser;
    private final int year;
    private final int month;
    private final TimeDataService source;

    public static void main(final String[] args) throws Exception {
        final Timeliste timeliste = new Timeliste("larsr", 2013, 1, new TimeDataServer());
        final Workbook workbook = timeliste.createTimeliste();
        timeliste.writeToFile("Timeliste", workbook);
    }


    public Timeliste(final String user, final int year, final int month, final TimeDataService source) {
        super();
        this.forUser = user;
        this.year = year;
        this.month = month;
        this.source = source;
    }

    public Workbook createTimeliste()  {
        final String sheetName = "Timeliste";
        final String headingTitle = "Timeliste";

        // Hent timedata for bruker
        final List<TimesheetEntry> fullList = this.source.forEmployee(this.forUser);
        // Filtrer for aktuelt tidsrom
        final List<TimesheetEntry> list = new ArrayList<>();
        for (final TimesheetEntry entry : fullList) {
            final LocalDate when = entry.getWhen();
            if (when.year().get() == this.year && when.monthOfYear().get() == this.month) list.add(entry);
        }
        // Grupper data
        final DoubleMatrix matrix = new DoubleMatrix();
        for (int i = 1; i < 31; i++) matrix.ensureCol(dayRef(i));
        for (final TimesheetEntry entry : list) {
            final int what = entry.getActivity();
            final double hours = (entry.getMinutes() / 30) / 2.0;
            final int day = entry.getWhen().getDayOfMonth();
            matrix.add(dayRef(day), "" + what, hours);
        }

        // Lag en arbeidsbok med 1 side
        final Workbook workbook = new XSSFWorkbook();
        final Sheet sheet = workbook.createSheet(sheetName);
        final PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);

        // Lag nødvendige stiler
        final Map<StyleName, CellStyle> styles = StyleFactory.styleSetup(workbook);

        // Hovedoverskrift
        int rownum = 0;
        int col = 0;
        final Row heading1 = createRow(sheet, rownum++, 45);
        Cell heading1cell = heading1.createCell(col++);
        heading1cell.setCellValue(headingTitle);
        heading1cell.setCellStyle(styles.get(StyleName.H1));
        heading1cell = heading1.createCell(col++);
        heading1cell.setCellValue(this.forUser);
        heading1cell.setCellStyle(styles.get(StyleName.H1));
        heading1cell = heading1.createCell(col++);
        heading1cell.setCellValue(this.year);
        heading1cell.setCellStyle(styles.get(StyleName.H1));
        heading1cell = heading1.createCell(col++);
        heading1cell.setCellValue(String.format("/ %02d", this.month));
        heading1cell.setCellStyle(styles.get(StyleName.H1));

        // Tabelloverskrift
        rownum++; // Hopp over en linje
        col = 0;
        final Row tableHead = createRow(sheet, rownum++, 40);
        final List<String> tableHeadings = new LinkedList<>();
        tableHeadings.add("Aktivitet");
        tableHeadings.add("Sum");
        tableHeadings.addAll(matrix.colKeys(true));
        for (final String header : tableHeadings) {
            final Cell headCell = tableHead.createCell(col++);
            headCell.setCellValue(header);
            headCell.setCellStyle(styles.get((col <3) ? StyleName.TBL_HEAD_LEFT : StyleName.TBL_HEAD));
        }

        // Datalinjer
        for (final String rKey : matrix.rowKeys(true)) {
            col = 0;
            final Row row = createRow(sheet, rownum++, -1);
            // Index
            final Cell cell1 = row.createCell(col++);
            cell1.setCellValue(rKey);
            cell1.setCellStyle(styles.get(StyleName.COL1));
            // Sum
            final Cell cellSum = row.createCell(col++);
            final String ref = SheetUtils.cellRef(3, rownum) + ":" + SheetUtils.cellRef(matrix.cSize() + 2, rownum);
            cellSum.setCellFormula("SUM(" + ref + ")");
            cellSum.setCellStyle(styles.get(StyleName.COLN));
            // Data
            for (final String c : matrix.colKeys(true)) {
                final Double v = matrix.get(c, rKey);
                final Cell cellx = row.createCell(col);
                cellx.setCellStyle(styles.get(StyleName.DATA));
                if (v != null) cellx.setCellValue(v);
                col++;
            }
        }

        // Sumlinje
        col = 0;
        final Row row = createRow(sheet, rownum++, -1);
        final Cell cell1 = row.createCell(col++);
        cell1.setCellValue("SUM");
        cell1.setCellStyle(styles.get(StyleName.SUM1));
        for (int i = 1; i < 31 + 1; i++) {
            final Cell cell = row.createCell(col++);
            final String ref = SheetUtils.cellRef(i + 1, 4) + ":" + SheetUtils.cellRef(i + 1, rownum - 1);
            cell.setCellFormula("SUM(" + ref + ")");
            cell.setCellStyle(styles.get(StyleName.SUMS));
        }

        // Format all columns
        sheet.setColumnWidth(0, sheet.getColumnWidth(0)*2);
        for (int i=1; i<=1+matrix.cSize(); i++) {
            sheet.autoSizeColumn(i);
        }

        return workbook;
    }

    public void writeToFile(final String bookName, final Workbook workbook) throws IOException {
        final String fileName = bookName + ".xlsx";
        final FileOutputStream out = new FileOutputStream(fileName);
        workbook.write(out);
        out.close();
    }

    private String dayRef(final int i) {
        return String.format("%02d.%02d", i, this.month);
    }

    private Row createRow(final Sheet sheet, final int rownum, final int height) {
        final Row heading1;
        heading1= sheet.createRow(rownum);
        if (height>0) heading1.setHeightInPoints(height);
        return heading1;
    }
}
