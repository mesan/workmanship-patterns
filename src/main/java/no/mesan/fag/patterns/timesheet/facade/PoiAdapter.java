package no.mesan.fag.patterns.timesheet.facade;

import java.util.HashMap;
import java.util.Map;

import no.mesan.fag.patterns.timesheet.format.StyleFactory.StyleName;
import no.mesan.fag.patterns.timesheet.format.StyleSpec;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/** Adapter vår fasade til POI Workbook. */
public class PoiAdapter {
    /// HINT Dette kan være start på en fasadeklasse - du må nok inn med mer
    private final Workbook workbook = new XSSFWorkbook();
    private final Sheet sheet;
    private final Map<StyleName, CellStyle> styles = new HashMap<>();

    public PoiAdapter(final String title, final Map<StyleName, StyleSpec> map) {
        sheet= workbook.createSheet(title);
        createWbStyles(map);
        printSetup();
    }

    /// HINT Skygger av løsning...
    private void printSetup() { }
    public void addData(final SpreadSheet values) {}
    public Workbook create() {return null;}

    /**
     * Konverter StyleSpecs til CellStyles.
     * @param map Map av navn til StyleSpec
     */
    private void createWbStyles(final Map<StyleName, StyleSpec> map){
        for (final Map.Entry<StyleName, StyleSpec> entry : map.entrySet()) {
            styles.put(entry.getKey(), entry.getValue().createStyle(workbook));
        }
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
}
