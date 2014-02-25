package no.mesan.fag.patterns.timesheet;

import no.mesan.fag.patterns.timesheet.data.DoubleMatrix;
import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.TimeDataService;
import no.mesan.fag.patterns.timesheet.external.TimeIteratorService;

import org.apache.poi.ss.usermodel.Workbook;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.Arrays;
import java.util.List;

/**
 * Timeliste for alle brukere for en enkelt uke.
 */
public class Ukeliste extends Sheets {
    public static final String SHEET_TITLE = "Ukeliste";

    private static final String[] DAYS = new String[] { "Man", "Tir", "Ons", "Tor", "Fre", "Lør", "Søn"};

    private final TimeDataService source;

    private final int year;
    private final LocalDate fromDate;
    private final LocalDate toDate;

    public Ukeliste(final int year, final int month, final int day, final TimeDataService source) {
        super();
        this.year = year;
        final LocalDate date = new LocalDate(year, month, day);
        this.fromDate = date.withDayOfWeek(DateTimeConstants.MONDAY);
        this.toDate = date.withDayOfWeek(DateTimeConstants.SUNDAY);
        this.source = source;
    }

    public Workbook createUkeliste()  {
        return generateReport(source, SHEET_TITLE, "Aktivitet/bruker -- dag", false);
    }

    @Override
    protected Iterable<TimesheetEntry> entryIterator(final TimeIteratorService service) {
        return service.forYear(this.year);
    }

    @Override
    protected boolean acceptData(final TimesheetEntry entry) {
        final LocalDate when = entry.getWhen();
        return !(when.isBefore(fromDate) || when.isAfter(toDate));
    }

    @Override
    protected void dataExtraHeadings(final DoubleMatrix matrix) {
        matrix.ensureCol(DAYS);
    }

    @Override
    protected List<String> headingTexts() {
        return Arrays.asList(SHEET_TITLE, fromDate.toString("yyyyMMdd"), "-", toDate.toString("yyyyMMdd"));
    }

    @Override
    protected String getRowRef(final TimesheetEntry entry) {
        return entry.getActivity() + " / " + entry.getUserID();
    }

    @Override
    protected String getColRef(final TimesheetEntry entry) {
        final int dayOfWeek = entry.getWhen().getDayOfWeek();
        return DAYS[dayOfWeek-1];
    }
}
