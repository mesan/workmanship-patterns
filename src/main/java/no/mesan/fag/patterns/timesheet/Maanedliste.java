package no.mesan.fag.patterns.timesheet;

import no.mesan.fag.patterns.timesheet.data.DoubleMatrix;
import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.TimeDataService;
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

/**
 * Hvem har fakturert på hvilke prosjekter i en gitt måned.
 * Aktiviteter >= 8000 ekskluderes.
 */
public class Maanedliste extends Sheets {
    public static final String SHEET_NAME = "Månedsliste";
    public static final String SHEET_TITLE = "Månedsoppgjør";
    private static final int INTERN_START = 8000;

    private final int year;
    private final int month;
    private final TimeDataService source;

    public Maanedliste(final int year, final int month, final TimeDataService source) {
        super();
        this.year = year;
        this.month = month;
        this.source = source;
    }

    public Workbook createMaanedliste()  {

        // Hent timedata for perioden
        final List<TimesheetEntry> fullList = this.source.forYear(this.year);
        // Filtrer bort interne timer og andre måneder
        final List<TimesheetEntry> list = new ArrayList<>();
        for (final TimesheetEntry entry : fullList) {
            if (entry.getActivity()< INTERN_START && entry.getWhen().monthOfYear().get() == this.month) list.add(entry);
        }
        // Grupper data
        final DoubleMatrix matrix = new DoubleMatrix();
        for (final TimesheetEntry entry : list) {
            final int what = entry.getActivity();
            final String who= entry.getUserID();
            final double hours = minutesToHours(entry);
            matrix.add("" + what, who, hours);
        }

        // Lag en arbeidsbok med 1 side
        final Workbook workbook = new XSSFWorkbook();
        final Sheet sheet = workbook.createSheet(SHEET_NAME);
        final PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);

        // Lag nødvendige stiler
        final Map<StyleFactory.StyleName, CellStyle> styles = StyleFactory.styleSetup(workbook);

        // Hovedoverskrift
        int rownum = 0;
        int col = 0;

        final Row heading1 = createRow(sheet, rownum++, 45);
        Cell heading1cell = heading1.createCell(col++);
        heading1cell.setCellValue(SHEET_TITLE);
        heading1cell.setCellStyle(styles.get(StyleFactory.StyleName.H1));
        heading1cell = heading1.createCell(col++);
        heading1cell.setCellValue(String.format("%04d/%02d", this.year, this.month));
        heading1cell.setCellStyle(styles.get(StyleFactory.StyleName.H1));

        // Tabelloverskrift
        rownum++; // Hopp over en linje
        col = 0;
        final Row tableHead = createRow(sheet, rownum++, 40);
        final List<String> tableHeadings = new LinkedList<>();
        tableHeadings.add("Bruker -- Aktivitet");
        tableHeadings.add("Sum");
        tableHeadings.addAll(matrix.colKeys(true));
        for (final String header : tableHeadings) {
            final Cell headCell = tableHead.createCell(col++);
            headCell.setCellValue(header);
            headCell.setCellStyle(styles.get((col <3) ? StyleFactory.StyleName.TBL_HEAD_LEFT : StyleFactory.StyleName.TBL_HEAD));
        }

        // Datalinjer
        for (final String rKey : matrix.rowKeys(true)) {
            col = 0;
            final Row row = createRow(sheet, rownum++, -1);
            // Index
            final Cell cell1 = row.createCell(col++);
            cell1.setCellValue(rKey);
            cell1.setCellStyle(styles.get(StyleFactory.StyleName.COL1));
            // Sum
            final Cell cellSum = row.createCell(col++);
            final String ref = cellRef(3, rownum) + ":" + cellRef(matrix.cSize() + 2, rownum);
            cellSum.setCellFormula("SUM(" + ref + ")");
            cellSum.setCellStyle(styles.get(StyleFactory.StyleName.COLN));
            // Data
            for (final String c : matrix.colKeys(true)) {
                final Double v = matrix.get(c, rKey);
                final Cell cellx = row.createCell(col);
                cellx.setCellStyle(styles.get(StyleFactory.StyleName.DATA));
                if (v != null) cellx.setCellValue(v);
                col++;
            }
        }

        // Sumlinje
        col = 0;
        final Row row = createRow(sheet, rownum++, -1);
        final Cell cell1 = row.createCell(col++);
        cell1.setCellValue("SUM");
        cell1.setCellStyle(styles.get(StyleFactory.StyleName.SUM1));
        for (int i = 1; i <= 1+matrix.cSize(); i++) {
            final Cell cell = row.createCell(col++);
            final String ref = cellRef(i + 1, 4) + ":" + cellRef(i + 1, rownum - 1);
            cell.setCellFormula("SUM(" + ref + ")");
            cell.setCellStyle(styles.get(StyleFactory.StyleName.SUMS));
        }

        // Rekalkuler
        final FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        for (Row r : sheet) {
            for (Cell c : r) {
                if (c.getCellType() == Cell.CELL_TYPE_FORMULA) {
                    evaluator.evaluateFormulaCell(c);
                }
            }
        }

        // Formatter alle kolonner
        for (int i=0; i< 2+matrix.cSize(); i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, (int) (1.05 * sheet.getColumnWidth(i)));
        }

        return workbook;
    }
}
