package no.mesan.fag.patterns.timesheet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import no.mesan.fag.patterns.timesheet.data.DoubleMatrix;
import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.TimeDataServer;
import no.mesan.fag.patterns.timesheet.external.TimeDataService;
import no.mesan.fag.patterns.timesheet.external.TimeSource;
import no.mesan.fag.patterns.timesheet.format.StyleFactory;
import no.mesan.fag.patterns.timesheet.format.StyleFactory.StyleName;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

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
    }

    /// HINT Foreslår følgende struktur på template-metoden:
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
        final Map<StyleName, CellStyle> styles = StyleFactory.styleSetup(workbook); // Lag nødvendige stiler

        createHeading(sheet, styles); // Hovedoverskrift
        createTableHead(sheet, matrix, styles, headTitle, sortedCols); // Tabelloverskrift
        createDataGrid(sheet, matrix, styles, sortedCols); // Datalinjer
        createSums(sheet, matrix, styles);  // Sumlinje
        finish(sheet, matrix.cSize());// Rekalkuler & reformatter

        return workbook;
    }



    protected List<TimesheetEntry> dataRetrieve(final TimeDataService dataService) {return null;}
    protected DoubleMatrix dataGroup(final List<TimesheetEntry> list) {return null;}
    protected Sheet createWorkbook(final String title) {return null;}
    protected void createHeading(final Sheet sheet, final Map<StyleName, CellStyle> styles) {}
    protected void createTableHead(final Sheet sheet, final DoubleMatrix matrix, final Map<StyleName, CellStyle> styles, final String headTitle, final boolean sortedCols) {}
    protected void createDataGrid(final Sheet sheet, final DoubleMatrix matrix, final Map<StyleName, CellStyle> styles, final boolean sortedCols) {}
    protected void createSums(final Sheet sheet, final DoubleMatrix matrix, final Map<StyleName, CellStyle> styles) {}
    protected void finish(final Sheet sheet, final int size) {}

    /**
     * Lag en cellereferanse (type A2B5) for en gitt kolonne+rad.
     * @param col Kolonne
     * @param row Rad
     * @return Celleref
     */
    String cellRef(final int col, final int row) {
        final int col0= col-1;
        final int ii= col0/26;
        final int i= col0%26;
        final StringBuilder b= new StringBuilder();
        if (ii>0) b.append((char) ('A'+ii-1));
        return b.append((char) ('A'+i))
                .append(row)
                .toString();
    }

    void writeToFile(final String bookName, final Workbook workbook) throws IOException {
        final String fileName = bookName + ".xlsx";
        final FileOutputStream out = new FileOutputStream(fileName);
        workbook.write(out);
        out.close();
    }

    Row createRow(final Sheet sheet, final int rownum, final int height) {
        final Row row;
        row= sheet.createRow(rownum);
        if (height>0) row.setHeightInPoints(height);
        return row;
    }

    double minutesToHours(final TimesheetEntry entry) {return (entry.getMinutes() / 30) / 2.0;}
}
