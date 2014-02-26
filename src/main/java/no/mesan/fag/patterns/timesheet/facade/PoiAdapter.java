package no.mesan.fag.patterns.timesheet.facade;

import no.mesan.fag.patterns.timesheet.data.ValueMatrix;
import no.mesan.fag.patterns.timesheet.format.StyleFactory.StyleName;
import no.mesan.fag.patterns.timesheet.format.StyleSpec;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/** Adapter vår fasade til POI Workbook. */
public class PoiAdapter {
    private final Workbook workbook = new XSSFWorkbook();
    private final Sheet sheet;
    private final Map<StyleName, CellStyle> styles = new HashMap<>();

    public PoiAdapter(final String title, final Map<StyleName, StyleSpec> map) {
        sheet= workbook.createSheet(title);
        createWbStyles(map);
        printSetup();
    }

    /**
     * Konverter StyleSpecs til CellStyles.
     * @param map Map av navn til StyleSpec
     */
    private void createWbStyles(final Map<StyleName, StyleSpec> map){
        for (final Map.Entry<StyleName, StyleSpec> entry : map.entrySet()) {
            styles.put(entry.getKey(), entry.getValue().createStyle(workbook));
        }
    }

    /** Sett utskriftsoppsett. */
    private void printSetup() {
        final PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);
    }

    public Workbook create() {
        final int noOfCols= reevaluateSheet();
        autoSizeSheet(noOfCols);
        return workbook;
    }

    /**
     * Evaluer alle formler på et gitt ark på nytt.
     * @return Antall kolonner
     */
    private int reevaluateSheet() {
        int maxCols= 0;
        final FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
        for (final Row r : sheet) {
            int cols= 0;
            for (final Cell c : r) {
                ++cols;
                if (c.getCellType() == Cell.CELL_TYPE_FORMULA) {
                    evaluator.evaluateFormulaCell(c);
                }
            }
            maxCols = Math.max(maxCols, cols);
        }
        return maxCols;
    }

    /**
     * Autojuster alle kolonner på et ark.
     * @param noOfColumns Antall kolonner i arket
     */
    private void autoSizeSheet(final int noOfColumns) {
        for (int i=0; i< noOfColumns; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, (int) (1.05*sheet.getColumnWidth(i)));
        }
    }

    /**
     * Legg til dataene
     * @param values Data
     */
    public void addData(final SpreadSheet values) {
        final ValueMatrix<Integer,Integer,SheetCell> data = values.getData();
        for (final int rowNum : data.rowKeys(true)) {
            createRow(data, rowNum, values.getRowHeight(rowNum));
        }
    }

    private void createRow(final ValueMatrix<Integer, Integer, SheetCell> data, final int rowNum,
                           final Integer height) {
        final Row row = sheet.createRow(rowNum);
        if (height!=null) row.setHeightInPoints(height);
        for (final int colNum : data.colKeys(true)) {
            createCell(data.get(colNum, rowNum), colNum, row);
        }
    }

    private void createCell(final SheetCell sheetCell, final int colNum,  final Row row) {
        if (sheetCell==null) return;
        final Cell cell = sheetCell.fillCell(row.createCell(colNum));
        if (sheetCell.hasStyle()) {
            cell.setCellStyle(styles.get(sheetCell.getStyle()));
        }
    }

    /**
     * Skriv en arbeidsbok til Excelfil.
     * @param bookName Boknavnet (suffikses med .xlsx)
     * @param workbook Referanse til boken
     * @throws java.io.IOException Verden er ikke perfekt
     */
    public static void writeToFile(final String bookName, final Workbook workbook) throws IOException {
        final String fileName = bookName + ".xlsx";
        final FileOutputStream out = new FileOutputStream(fileName);
        workbook.write(out);
        out.close();
    }
}
