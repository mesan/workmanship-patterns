package no.mesan.fag.patterns.timesheet;

import java.util.Arrays;
import java.util.List;

import no.mesan.fag.patterns.timesheet.data.DoubleMatrix;
import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.TimeDataService;
import no.mesan.fag.patterns.timesheet.external.TimeIteratorService;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.LocalDate;

/** Timeliste for en enkelt bruker for en måned. */
public class Timeliste extends Sheets {
    public static final String SHEET_TITLE = "Timeliste";

    private final String forUser;
    private final int year;
    private final int month;
    private final TimeDataService source;

    public Timeliste(final String user, final int year, final int month, final TimeDataService source) {
        this.forUser = user;
        this.year = year;
        this.month = month;
        this.source = source;
    }

    public Workbook createTimeliste()  {
        return generateReport(source, SHEET_TITLE, "Aktivitet", true);
    }

    @Override
    protected Iterable<TimesheetEntry> entryIterator(final TimeIteratorService service) {
        return service.forEmployee(forUser);
    }

    @Override
    protected boolean acceptData(final TimesheetEntry entry) {
        final LocalDate when = entry.getWhen();
        return when.year().get() == year && when.monthOfYear().get() == month;
    }

    @Override
    protected void dataExtraHeadings(final DoubleMatrix matrix) {
        final int maxDays= new LocalDate(year, month, 1).dayOfMonth().getMaximumValue();
        for (int i = 1; i < maxDays; i++) matrix.ensureCol(dayRef(i));
    }

    @Override
    protected List<String> headingTexts() {
        return Arrays.asList(SHEET_TITLE, forUser, String.format("%04d", year),
                             String.format("/ %02d", month));
    }

    @Override
    protected String getRowRef(final TimesheetEntry entry) {
        return ""+ entry.getActivity();
    }

    @Override protected String getColRef(final TimesheetEntry entry) {
        return dayRef(entry.getWhen().getDayOfMonth());
    }

    private String dayRef(final int i) {
        return String.format("%02d.%02d", i, month);
    }
}
