package no.mesan.fag.patterns.timesheet.external.decorators;

import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.TimeDataService;

/** Dekoratør for den eksterne tjenesten som legger på litt fancy logging. */
public class TimeDataServiceLoggingDecorator extends TimeDataServiceDecorator {

    private Logger logger = Logger.getLogger("TimeDataServiceLoggingDecorator");

    public TimeDataServiceLoggingDecorator(final TimeDataService timeDataService) {
        super(timeDataService);
    }

    /** Setter for test. */
    void setLogger(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public List<TimesheetEntry> forEmployee(final String userID, final int from) {
        logger.info(String.format("Parameters: userID=%s, from=%d", userID, from));
        return executeWithLog(()-> timeDataService.forEmployee(userID, from));
    }

    @Override
    public List<TimesheetEntry> forYear(final int year, final int from) {
        logger.info(String.format("Parameters: year=%d, from=%d", year, from));
        return executeWithLog(()-> timeDataService.forYear(year, from));
    }

    private List<TimesheetEntry> executeWithLog(final Supplier<List<TimesheetEntry>> func) {
        final long start = System.currentTimeMillis();
        final List<TimesheetEntry> timesheetEntries = func.get();
        final long timeSpent = System.currentTimeMillis() - start;

        logger.info(String.format("Found %d entries, in %dms", timesheetEntries.size(), timeSpent));
        return timesheetEntries;
    }
}
