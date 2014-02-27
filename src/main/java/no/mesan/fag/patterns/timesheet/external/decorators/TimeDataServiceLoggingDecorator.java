package no.mesan.fag.patterns.timesheet.external.decorators;

import java.util.List;
import java.util.logging.Logger;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.TimeDataService;


/**
 * Dekoratør for den eksterne tjenesten som legger på litt fancy logging.
 */
public class TimeDataServiceLoggingDecorator extends TimeDataServiceDecorator {

    private static final Logger LOG = Logger.getLogger("TimeDataServiceLoggingDecorator");

    public TimeDataServiceLoggingDecorator(final TimeDataService timeDataService) {
        super(timeDataService);
    }

    @Override
    public List<TimesheetEntry> forEmployee(final String userID, final int from) {
        LOG.info(String.format("Parameters: userID=%s, from=%d", userID, from));

        final long start = System.currentTimeMillis();
        final List<TimesheetEntry> timesheetEntries = timeDataService.forEmployee(userID, from);
        final long timeSpent = System.currentTimeMillis() - start;

        LOG.info(String.format("Found %d entries, in %dms", timesheetEntries.size(), timeSpent));

        return timesheetEntries;
    }

    // Den skarpøyde vil se at her ville lambdaer/funksjonelle interfacer fra Java 8 vært kjekt for å pent unngå
    // repeterende kode
    @Override
    public List<TimesheetEntry> forYear(final int year, final int from) {
        LOG.info(String.format("Parameters: year=%d, from=%d", year, from));

        final long start = System.currentTimeMillis();
        final List<TimesheetEntry> timesheetEntries = timeDataService.forYear(year, from);
        final long timeSpent = System.currentTimeMillis() - start;

        LOG.info(String.format("Found %d entries, in %dms", timesheetEntries.size(), timeSpent));

        return timesheetEntries;
    }
}
