package no.mesan.fag.patterns.timesheet;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.TimeDataService;
import no.mesan.fag.patterns.timesheet.external.TimeIteratorService;

import org.apache.poi.ss.usermodel.Workbook;

import java.util.Arrays;
import java.util.List;

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
        this.year = year;
        this.month = month;
        this.source = source;
    }

    @Override
    public Workbook createBook() {
        return createMaanedliste();
    }

    public Workbook createMaanedliste()  {
        return generateReport(source, SHEET_NAME, "Bruker -- Aktivitet", true);
    }

    @Override
    protected Iterable<TimesheetEntry> entryIterator(final TimeIteratorService service) {
        return service.forYear(year);
    }

    @Override
    protected boolean acceptData(final TimesheetEntry entry) {
        return entry.getActivity()< INTERN_START && entry.getWhen().monthOfYear().get() == month;
    }

    @Override
    protected List<String> headingTexts() {
        return Arrays.asList(SHEET_TITLE, String.format("%04d/%02d", year, month));
    }

    @Override
    protected String getRowRef(final TimesheetEntry entry) {
        return entry.getUserID();
    }

    @Override
    protected String getColRef(final TimesheetEntry entry) {
        return ""+ entry.getActivity();
    }
}
