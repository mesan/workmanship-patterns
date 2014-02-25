package no.mesan.fag.patterns.timesheet;

import no.mesan.fag.patterns.timesheet.data.DoubleMatrix;
import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.TimeDataService;
import no.mesan.fag.patterns.timesheet.external.TimeIteratorService;

import org.apache.poi.ss.usermodel.Workbook;

import java.util.Arrays;
import java.util.List;

/**
 * Timer per prosjekt per måned over et år.
 */
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
        return generateReport(source, SHEET_TITLE, "Aktivitet -- Måned", true);
    }

    @Override
    protected Iterable<TimesheetEntry> entryIterator(final TimeIteratorService service) {
        return service.forYear(this.year);
    }
    @Override
    protected void dataExtraHeadings(final DoubleMatrix matrix) {
        for (int i = 1; i < 12; i++) matrix.ensureCol(String.format("%02d", i));
    }
    @Override
    protected List<String> headingTexts() {
        return Arrays.asList(SHEET_TITLE, String.format("%04d", this.year));
    }
    @Override protected String getRowRef(final TimesheetEntry entry) {
        return ""+ entry.getActivity();
    }
    @Override protected String getColRef(final TimesheetEntry entry) {
        return String.format("%02d", entry.getWhen().getMonthOfYear());
    }
}
