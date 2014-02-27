package no.mesan.fag.patterns.timesheet;

import no.mesan.fag.patterns.timesheet.data.DoubleMatrix;
import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.TimeDataService;
import no.mesan.fag.patterns.timesheet.external.TimeIteratorService;
import no.mesan.fag.patterns.timesheet.format.StyleFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** Timer per prosjekt per måned over et år. */
public class Aarsliste extends Sheets {
    public static final String SHEET_TITLE = "Årsoversikt";

    private final int year;
    private final TimeDataService source;

    public Aarsliste(final int year, final TimeDataService source) {
        super();
        this.year = year;
        this.source = source;
    }

    public Workbook createAarsoversikt() {

        // Hent timedata for året, ingen filtrering
        final List<TimesheetEntry> list = new ArrayList<>();
        final Iterable<TimesheetEntry> entries = new TimeIteratorService(source).forYear(this.year);
        for (final TimesheetEntry entry: entries) {
            list.add(entry);
        }

        // Grupper data
        final DoubleMatrix matrix = new DoubleMatrix();
        for (int i = 1; i < 12; i++) matrix.ensureCol(String.format("%02d", i));
        for (final TimesheetEntry entry : list) {
            final int what = entry.getActivity();
            final String month = String.format("%02d", entry.getWhen().getMonthOfYear());
            final double hours = minutesToHours(entry);
            matrix.add(month, "" + what, hours);
        }

        // Lag en arbeidsbok med 1 side
        final Workbook workbook = new XSSFWorkbook();
        final Sheet sheet = workbook.createSheet(SHEET_TITLE);
        final PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);

        // Lag nødvendige stiler
        final Map<StyleFactory.StyleName, CellStyle> styles = StyleFactory.styleSetup(workbook);

        // Hovedoverskrift
        int rownum = 0;
        int colnum = 0;

        final Row heading1 = createRow(sheet, rownum++, 45);
        Cell heading1cell = heading1.createCell(colnum++);
        heading1cell.setCellValue(SHEET_TITLE);
        heading1cell.setCellStyle(styles.get(StyleFactory.StyleName.H1));
        heading1cell = heading1.createCell(colnum++);
        heading1cell.setCellValue(String.format("%04d", this.year));
        heading1cell.setCellStyle(styles.get(StyleFactory.StyleName.H1));

        // Tabelloverskrift
        rownum++; // Hopp over en linje
        colnum = 0;
        final Row tableHead = createRow(sheet, rownum++, 40);
        final List<String> tableHeadings = new LinkedList<>();
        tableHeadings.add("Aktivitet -- Måned");
        tableHeadings.add("Sum");
        tableHeadings.addAll(matrix.colKeys(true));
        for (final String header : tableHeadings) {
            final Cell headCell = tableHead.createCell(colnum++);
            headCell.setCellValue(header);
            headCell.setCellStyle(styles.get((colnum < 3) ? StyleFactory.StyleName.TBL_HEAD_LEFT : StyleFactory.StyleName.TBL_HEAD));
        }

        // Datalinjer
        for (final String rKey : matrix.rowKeys(true)) {
            colnum = 0;
            final Row row = createRow(sheet, rownum++, -1);
            // Index
            final Cell cell1 = row.createCell(colnum++);
            cell1.setCellValue(rKey);
            cell1.setCellStyle(styles.get(StyleFactory.StyleName.COL1));
            // Sum
            final Cell cellSum = row.createCell(colnum++);
            final String ref = cellRef(3, rownum) + ":" + cellRef(matrix.cSize() + 2, rownum);
            cellSum.setCellFormula("SUM(" + ref + ")");
            cellSum.setCellStyle(styles.get(StyleFactory.StyleName.COLN));
            // Data
            for (final String c : matrix.colKeys(true)) {
                final Double v = matrix.get(c, rKey);
                final Cell cellx = row.createCell(colnum);
                cellx.setCellStyle(styles.get(StyleFactory.StyleName.DATA));
                if (v != null) cellx.setCellValue(v);
                colnum++;
            }
        }

        // Sumlinje
        colnum = 0;
        final Row row = createRow(sheet, rownum++, -1);
        final Cell cell1 = row.createCell(colnum++);
        cell1.setCellValue("SUM");
        cell1.setCellStyle(styles.get(StyleFactory.StyleName.SUM1));
        for (int i = 1; i <= 1 + matrix.cSize(); i++) {
            final Cell cell = row.createCell(colnum++);
            final String ref = cellRef(i + 1, 4) + ":" + cellRef(i + 1, rownum - 1);
            cell.setCellFormula("SUM(" + ref + ")");
            cell.setCellStyle(styles.get(StyleFactory.StyleName.SUMS));
        }

        // Rekalkuler
        final FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        for (final Row r : sheet) {
            for (final Cell c : r) {
                if (c.getCellType() == Cell.CELL_TYPE_FORMULA) {
                    evaluator.evaluateFormulaCell(c);
                }
            }
        }

        // Formatter alle kolonner
        for (int i = 0; i < 2 + matrix.cSize(); i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, (int) (1.05 * sheet.getColumnWidth(i)));
        }

        return workbook;
    }
}
