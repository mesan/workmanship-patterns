package no.mesan.fag.patterns.timesheet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import no.mesan.fag.patterns.timesheet.data.DoubleMatrix;
import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.TimeDataServer;
import no.mesan.fag.patterns.timesheet.external.TimeDataService;
import no.mesan.fag.patterns.timesheet.external.TimeIteratorService;
import no.mesan.fag.patterns.timesheet.external.TimeSource;
import no.mesan.fag.patterns.timesheet.facade.SheetCell;
import no.mesan.fag.patterns.timesheet.format.StyleFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/** Superklasse for timelister. */
public abstract class Sheets {

    public static void main(final String[] args) throws Exception {
//      ColorSpec.setTheme(ColorSpec.Theme.RED);
        final TimeDataServer source = new TimeDataServer(new TimeSource());
        final Timeliste timeliste = new Timeliste("larsr", 2014, 2, source);
        final Workbook wb1 = timeliste.createTimeliste();
        timeliste.writeToFile("Timeliste", wb1);
        final Maanedliste mndListe = new Maanedliste(2014, 2, source);
        final Workbook wb2 = mndListe.createMaanedliste();
        mndListe.writeToFile("Månedsoppgjør", wb2);
        final Aarsliste aarsListe = new Aarsliste(2014, source);
        final Workbook wb3 = aarsListe.createAarsoversikt();
        aarsListe.writeToFile("Årsoversikt", wb3);
        final Ukeliste ukeListe = new Ukeliste(2014, 1, 15, source);
        final Workbook wb4 = ukeListe.createUkeliste();
        ukeListe.writeToFile("Ukeoversikt", wb4);
    }

    /**
     * Dette er hovedrutinen for å lage rapporter.
     * Du må selvfølgelig overstyre alle abstrakte rutiner (! under), men du kan også gripe inn på andre nivåer --
     * her er strukturen:
     * <pre>
     *     dataRetrieve
     *       entryIterator!
     *       acceptData -- filterfunksjon, aksepterer default alt
     *     dataGroup
     *       dataExtraHeadings -- evt. tilleggsoverskrifter, default ingen
     *       getRowRef!
     *       getColRef!
     *     createWorkbook
     *     createHeading
     *       headingTexts!
     *     createTableHead
     *     createDataGrid
     *     createSums
     *     finish
     * </pre>
     *
     * @param dataService Tjeneste for å hente data
     * @param title Navn på arbeidsboken
     * @param headTitle Øverste venstre overskrift i tabellen
     * @param sortedCols Om kolonneoverskriftene skal være sortert
     * @return Resultat
     */
    protected final Workbook generateReport(final TimeDataService dataService, final String title,
                                            final String headTitle, final boolean sortedCols)  {
        final List<TimesheetEntry> list = dataRetrieve(dataService); // Hent timedata og filtrer
        final DoubleMatrix matrix = dataGroup(list); // Grupper data

        final Sheet sheet = createWorkbook(title); // Lag en arbeidsbok med 1 side
        final Workbook workbook= sheet.getWorkbook();
        final Map<StyleFactory.StyleName, CellStyle> styles = StyleFactory.styleSetup(workbook); // Lag nødvendige stiler

        createHeading(sheet, styles); // Hovedoverskrift
        createTableHead(sheet, matrix, styles, headTitle, sortedCols); // Tabelloverskrift
        createDataGrid(sheet, matrix, styles, sortedCols); // Datalinjer
        createSums(sheet, matrix, styles);  // Sumlinje
        finish(sheet, matrix.cSize());// Rekalkuler & reformatter

        return workbook;
    }

    /**
     * Hent og filtrer data.
     * @param dataService Datakilde
     * @return Liste av entries som skal være med
     */
    protected List<TimesheetEntry> dataRetrieve(final TimeDataService dataService) {
        final TimeIteratorService service = new TimeIteratorService(dataService);
        return StreamSupport.stream(entryIterator(service).spliterator(), true)
                       .filter(entry -> acceptData(entry)).collect(Collectors.toList());
    }

    /**
     * Sett opp iteratoren for data fra eksternt system.
     * @param service Datakilde
     * @return Iterator for data
     */
    protected abstract Iterable<TimesheetEntry> entryIterator(TimeIteratorService service);

    /**
     * Filtrer data.
     * @param entry Et entry
     * @return true hvis denne skal være med i resultat
     */
    protected boolean acceptData(final TimesheetEntry entry) {
        return true;
    }

    /**
     * Grupper og summer data i kolonner og rader.
     * @param list Alle entries
     * @return Matrise med summerte data
     */
    protected DoubleMatrix dataGroup(final List<TimesheetEntry> list) {
        final DoubleMatrix matrix = new DoubleMatrix();
        dataExtraHeadings(matrix);
        list.stream().forEach(entry ->
            matrix.add(getColRef(entry), getRowRef(entry), minutesToHours(entry)));
        return matrix;
    }

    /**
     * Hvilken kolonne skal denne legges i?
     * @param entry Data som skal grupperes
     * @return Kolonnenavn
     */
    protected abstract String getColRef(TimesheetEntry entry);

    /**
     * Hvilken rad skal denne legges i?
     * @param entry Data som skal grupperes
     * @return Radnavn
     */
    protected abstract String getRowRef(TimesheetEntry entry);

    /**
     * Legg på evt faste headinger.
     * @param matrix Matrisen (oppdateres ved behov)
     */
    protected void dataExtraHeadings(final DoubleMatrix matrix) {
        return;
    }

    /**
     * Opprett selve arbeidsboken med 1 ark.
     * @param title Navn på arket
     * @return Arket (denne gir oss tilgang til boken ved behov)
     */
    protected Sheet createWorkbook(final String title) {
        final Workbook workbook = new XSSFWorkbook();
        final Sheet sheet = workbook.createSheet(title);
        final PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);
        return sheet;
    }

    /**
     * Lag overskriftslinjen øverst i arket.
     * @param sheet Arket
     * @param styles Gir tilgang til nødvendige stiler
     */
    protected void createHeading(final Sheet sheet, final Map<StyleFactory.StyleName, CellStyle> styles) {
        int colnum= 0;
        final Row row = makeRow(sheet, 0, 45);
        final CellStyle style = styles.get(StyleFactory.StyleName.H1);
        for (final String text : headingTexts()) {
            final Cell cell = row.createCell(colnum++);
            cell.setCellValue(text);
            cell.setCellStyle(style);
        }
    }

    /**
     * Returner en liste over tekstene i toppoverskriften.
     * @return Som nevnt...
     */
    protected abstract List<String> headingTexts();

    /**
     * Lag tabelloverskriftene.
     * @param sheet Arket
     * @param matrix Dataene
     * @param styles Stilene
     * @param headTitle Teksten i øverste venstre hjørne
     * @param sortedCols Om kolonnene skal sorteres
     */
    protected void createTableHead(final Sheet sheet, final DoubleMatrix matrix,
                                   final Map<StyleFactory.StyleName, CellStyle> styles, final String headTitle,
                                   final boolean sortedCols) {
        int colnum= 0;
        final Row tableHead = makeRow(sheet, 2, 40);
        final List<String> tableHeadings = new LinkedList<>();
        tableHeadings.add(headTitle);
        tableHeadings.add("Sum");
        tableHeadings.addAll(matrix.colKeys(sortedCols));
        for (final String header : tableHeadings) {
            final Cell headCell = tableHead.createCell(colnum++);
            headCell.setCellValue(header);
            headCell.setCellStyle(styles.get((colnum <3) ? StyleFactory.StyleName.TBL_HEAD_LEFT
                                                         : StyleFactory.StyleName.TBL_HEAD));
        }
    }

    /**
     * Skriv inn alle data (uten sum eller overskrifter)
     * @param sheet Arket
     * @param matrix Dataene
     * @param styles Stilene
     * @param sortedCols Om kolonnene skal sorteres
     */
    protected void createDataGrid(final Sheet sheet, final DoubleMatrix matrix,
                                  final Map<StyleFactory.StyleName, CellStyle> styles,
                                  final boolean sortedCols) {
        int rownum= 3; // Hopp over rad
        for (final String rKey : matrix.rowKeys(true)) {
            int colnum = 0;
            final Row row = makeRow(sheet, rownum++, -1);
            // Index
            final Cell cell1 = row.createCell(colnum++);
            cell1.setCellValue(rKey);
            cell1.setCellStyle(styles.get(StyleFactory.StyleName.COL1));
            // Sum
            final Cell cellSum = row.createCell(colnum++);
            final String ref = SheetCell.cellRef(3, rownum) + ":" + SheetCell.cellRef(matrix.cSize() + 2, rownum);
            cellSum.setCellFormula("SUM(" + ref + ")");
            cellSum.setCellStyle(styles.get(StyleFactory.StyleName.COLN));
            // Data
            final CellStyle dataStyle = styles.get(StyleFactory.StyleName.DATA);
            for (final String c : matrix.colKeys(sortedCols)) {
                final Double v = matrix.get(c, rKey);
                final Cell cellx = row.createCell(colnum);
                cellx.setCellStyle(dataStyle);
                if (v != null) cellx.setCellValue(v);
                colnum++;
            }
        }
    }

    /**
     * Sett inn sumlinjen.
     * @param sheet Arket
     * @param matrix Dataene
     * @param styles Stilene
     */
    protected void createSums(final Sheet sheet, final DoubleMatrix matrix,
                              final Map<StyleFactory.StyleName, CellStyle> styles) {
        int colnum = 0;
        final int rownum= sheet.getLastRowNum();
        final Row row = makeRow(sheet, rownum+1, -1);
        final Cell cell1 = row.createCell(colnum++);
        cell1.setCellValue("SUM");
        cell1.setCellStyle(styles.get(StyleFactory.StyleName.SUM1));
        for (int i = 1; i <= 1+matrix.cSize(); i++) {
            final Cell cell = row.createCell(colnum++);
            final String ref = SheetCell.cellRef(i + 1, 4) + ":" + SheetCell.cellRef(i + 1, rownum + 1);
            cell.setCellFormula("SUM(" + ref + ")");
            cell.setCellStyle(styles.get(StyleFactory.StyleName.SUMS));
        }
    }

    /**
     * Rekalkuler formler, juster bredder.
     * @param sheet Arket
     * @param dataCols Antall datakolonner (uten ledetekst/sum)
     */
    protected void finish(final Sheet sheet, final int dataCols) {
        final Workbook workbook= sheet.getWorkbook();
        final FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        for (final Row r : sheet) {
            for (final Cell c : r) {
                if (c.getCellType() == Cell.CELL_TYPE_FORMULA) {
                    evaluator.evaluateFormulaCell(c);
                }
            }
        }

        // Formatter alle kolonner
        for (int i=0; i< 2+dataCols; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, (int) (1.05*sheet.getColumnWidth(i)));
        }
    }

    /**
     * Skriv resultatet til Excelfil.
     * @param bookName Boknavnet (suffikses med .xlsx)
     * @param workbook Referanse til boken
     * @throws IOException Verden er ikke perfekt
     */
    protected void writeToFile(final String bookName, final Workbook workbook) throws IOException {
        final String fileName = bookName + ".xlsx";
        final FileOutputStream out = new FileOutputStream(fileName);
        workbook.write(out);
        out.close();
    }

    /**
     * Lag en ny rad.
     * @param sheet Arket
     * @param rownum Radnummer
     * @param height Ønsket høyde (<=0 for å bruke default)
     * @return Den nye raden
     */
    protected Row makeRow(final Sheet sheet, final int rownum, final int height) {
        final Row row;
        row= sheet.createRow(rownum);
        if (height>0) row.setHeightInPoints(height);
        return row;
    }

    /**
     * Konverter minutter til et antall timer (men vi regner bare med fulle halvtimer).
     * @param entry Original
     * @return Timer
     */
    protected double minutesToHours(final TimesheetEntry entry) {
        return (entry.getMinutes() / 30) / 2.0;
    }
}
