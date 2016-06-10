package no.mesan.fag.patterns.timesheet;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import no.mesan.fag.patterns.timesheet.command.AsyncTask;
import no.mesan.fag.patterns.timesheet.command.AsyncTaskExecutor;
import no.mesan.fag.patterns.timesheet.data.DoubleMatrix;
import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.TimeDataServer;
import no.mesan.fag.patterns.timesheet.external.TimeDataService;
import no.mesan.fag.patterns.timesheet.external.TimeIteratorService;
import no.mesan.fag.patterns.timesheet.external.TimeSource;
import no.mesan.fag.patterns.timesheet.external.decorators.TimeDataServiceCachingDecorator;
import no.mesan.fag.patterns.timesheet.external.decorators.TimeDataServiceLoggingDecorator;
import no.mesan.fag.patterns.timesheet.facade.DoubleCell;
import no.mesan.fag.patterns.timesheet.facade.EmptyCell;
import no.mesan.fag.patterns.timesheet.facade.FormulaCell;
import no.mesan.fag.patterns.timesheet.facade.PoiAdapter;
import no.mesan.fag.patterns.timesheet.facade.SpreadSheet;
import no.mesan.fag.patterns.timesheet.facade.StringCell;
import no.mesan.fag.patterns.timesheet.format.ColorSpec.Theme;
import no.mesan.fag.patterns.timesheet.format.StyleFactory;
import no.mesan.fag.patterns.timesheet.format.StyleFactory.StyleName;
import no.mesan.fag.patterns.timesheet.format.StyleSpec;
import no.mesan.fag.patterns.timesheet.strategy.TimeRepresentationDays;
import no.mesan.fag.patterns.timesheet.strategy.TimeRepresentationHalfHours;
import no.mesan.fag.patterns.timesheet.strategy.TimeRepresentationStrategy;
import org.apache.poi.ss.usermodel.Workbook;

/** Superklasse for timelister. */
public abstract class Sheets {

    /** Hvordan vi viser timer på listene. */
    private TimeRepresentationStrategy timeRepresentationStrategy= new TimeRepresentationHalfHours();

    /** Ønsket fargestil. */
    private Theme theme= Theme.BLUE;

    private static final class TimelisteTask implements AsyncTask {

        private final String name;
        private final Sheets sheet;

        private TimelisteTask(final String name, final Sheets sheet) {
            this.sheet = sheet;
            this.name = name;
        }

        @Override
        public String whoAmI() {
            return name;
        }

        @Override
        public void executeTask() {
            try {
                final Workbook wb = sheet.createBook();
                sheet.writeToFile(name, wb);
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(final String[] args) throws Exception {
        final TimeDataServer source = new TimeDataServer(new TimeSource());
        final Timeliste timeliste = new Timeliste("larsr", 2014, 2, source);
        final Maanedliste mndListe = new Maanedliste(2014, 2, source);
        final Aarsliste aarsListe = new Aarsliste(2014, source);
        final Ukeliste ukeListe =
                new Ukeliste(2014, 1, 15,
                             new TimeDataServiceLoggingDecorator(new TimeDataServiceCachingDecorator(source)));
        ukeListe.setTimeRepresentationStrategy(new TimeRepresentationDays());
        ukeListe.setTheme(Theme.RED);

        final AsyncTaskExecutor taskExecutor = new AsyncTaskExecutor();
        taskExecutor.executeTasks(new TimelisteTask("Timeliste", timeliste),
                                  new TimelisteTask("Månedsoppgjør", mndListe),
                                  new TimelisteTask("Årsoversikt", aarsListe),
                                  new TimelisteTask("Ukeoversikt", ukeListe));
    }

    /** Opprett arbeidsbok. */
    protected abstract Workbook createBook();

    /**
     * Sett ny strategi for representasjon av timer.
     * @param timeRepresentationStrategy Strategi
     */
    public void setTimeRepresentationStrategy(final TimeRepresentationStrategy timeRepresentationStrategy) {
        this.timeRepresentationStrategy = timeRepresentationStrategy;
    }

    /** Sett ønsket fargesetting .*/
    public void setTheme(final Theme theme) {
        this.theme = theme;
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
    final Workbook generateReport(final TimeDataService dataService, final String title,
                                  final String headTitle, final boolean sortedCols)  {
        final List<TimesheetEntry> list = dataRetrieve(dataService); // Hent timedata og filtrer
        final DoubleMatrix matrix = dataGroup(list); // Grupper data

        final SpreadSheet sheet = new SpreadSheet(title);
        sheet.setTheme(theme);
        final Map<StyleName, StyleSpec> styles = StyleFactory.styleSetup(); // Lag nødvendige stiler

        createHeading(sheet); // Hovedoverskrift
        createTableHead(sheet, matrix, headTitle, sortedCols); // Tabelloverskrift
        createDataGrid(sheet, matrix, sortedCols); // Datalinjer
        createSums(sheet, matrix);  // Sumlinje
        return finish(title, sheet, styles); // Konverter til Workbook
    }

    /**
     * Hent og filtrer data.
     * @param dataService Datakilde
     * @return Liste av entries som skal være med
     */
    protected List<TimesheetEntry> dataRetrieve(final TimeDataService dataService) {
        final TimeIteratorService service = new TimeIteratorService(dataService);
        return StreamSupport.stream(entryIterator(service).spliterator(), true)
                       .filter(entry-> acceptData(entry)).collect(Collectors.toList());
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
    boolean acceptData(final TimesheetEntry entry) {
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
            matrix.add(getColRef(entry), getRowRef(entry), minutesToCorrectRepresentation(entry)));
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
    @SuppressWarnings({"UnnecessaryReturnStatement", "EmptyMethod"})
    void dataExtraHeadings(final DoubleMatrix matrix) {
        return;
    }

    /**
     * Lag overskriftslinjen øverst i arket.
     * @param sheet Arket
     */
    void createHeading(final SpreadSheet sheet) {
        final int rownum= 0;
        int colnum= 0;
        sheet.setRowHeight(rownum, 45);
        for (final String text : headingTexts()) {
            sheet.setCell(colnum++, rownum, new StringCell(text, StyleName.H1));
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
     * @param headTitle Teksten i øverste venstre hjørne
     * @param sortedCols Om kolonnene skal sorteres
     */
    void createTableHead(final SpreadSheet sheet, final DoubleMatrix matrix,
                         final String headTitle, final boolean sortedCols) {
        final int rownum= 2;
        int colnum= 0;
        sheet.setRowHeight(rownum, 40);
        final List<String> tableHeadings = new LinkedList<>();
        tableHeadings.add(headTitle);
        tableHeadings.add("Sum");
        tableHeadings.addAll(matrix.colKeys(sortedCols));
        for (final String header : tableHeadings) {
            sheet.setCell(colnum++, rownum,
                          new StringCell(header, (colnum < 3) ? StyleName.TBL_HEAD_LEFT : StyleName.TBL_HEAD));
        }
    }

    /**
     * Skriv inn alle data (uten sum eller overskrifter)
     * @param sheet Arket
     * @param matrix Dataene
     * @param sortedCols Om kolonnene skal sorteres
     */
    void createDataGrid(final SpreadSheet sheet, final DoubleMatrix matrix, final boolean sortedCols) {
        int rownum = 1;
        sheet.getData().ensureRow(rownum++);  // Hopp over rad
        for (final String rKey : matrix.rowKeys(true)) {
            rownum++;
            int colnum = 0;
            sheet.setCell(colnum++, rownum, new StringCell(rKey, StyleName.COL1)); // Index
            sheet.setCell(colnum, rownum,
                          FormulaCell.formulaSUM(colnum + 2, rownum + 1, 2 + matrix.cSize(), rownum + 1,
                                                 StyleName.COLN)); // Sum
            for (final String c : matrix.colKeys(sortedCols)) { // Data
                final Double v = matrix.get(c, rKey);
                sheet.setCell(++colnum, rownum, (v == null) ? new EmptyCell(StyleName.DATA)
                                                            : new DoubleCell(v, StyleName.DATA));
            }
        }
    }

    /**
     * Sett inn sumlinjen.
     * @param sheet Arket
     * @param matrix Dataene
     */
    void createSums(final SpreadSheet sheet, final DoubleMatrix matrix) {
        final int rownum= sheet.lastRowNum();
        sheet.setCell(0, rownum, new StringCell("SUM", StyleName.SUM1));
        for (int i = 1; i <= 1+matrix.cSize(); i++) {
            sheet.setCell(i, rownum, FormulaCell.formulaSUM(i+1, 4, i+1, rownum, StyleName.SUMS));
        }
    }

    /**
     * Lag arbeidsbok!
     * @param title Navn på arket
     * @param values Innholdet i boka
     * @param styles Stilene som skal inn
     */
    Workbook finish(final String title, final SpreadSheet values, final Map<StyleName, StyleSpec> styles) {
        return new PoiAdapter(title, values, styles).create();
    }

    /**
     * Skriv resultatet til Excelfil.
     * @param bookName Boknavnet (suffikses med .xlsx)
     * @param workbook Referanse til boken
     * @throws IOException Verden er ikke perfekt
     */
    void writeToFile(final String bookName, final Workbook workbook) throws IOException {
        PoiAdapter.writeToFile(bookName, workbook);
    }

    /**
     * Konverter minutter til korrekt visningsverdi via den strategien klassen er instansiert med.
     * @param entry Original
     * @return Timer
     */
    double minutesToCorrectRepresentation(final TimesheetEntry entry) {
        return timeRepresentationStrategy.convert(entry.getMinutes());
    }
}
