package no.mesan.fag.patterns.timesheet.external.decorators;

import java.util.List;
import java.util.logging.Logger;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.TimeDataService;

/// HINT starten på en klasse som kan logge kall til tjenesten
/** Dekoratør for den eksterne tjenesten som legger på litt fancy logging. */
public class TimeDataServiceLoggingDecorator extends TimeDataServiceDecorator {

    private Logger logger = Logger.getLogger("TimeDataServiceLoggingDecorator");

    public TimeDataServiceLoggingDecorator(final TimeDataService cachingDecoratedService) {
        super(cachingDecoratedService);
    }


    /// HINT type logging vi ønsker
    // Før kall: logger.info(String.format("Parameters: userID=%s, from=%d", userID, from));
    // Etter kall:  logger.info(String.format("Found %d entries, in %dms", timesheetEntries.size(), timeSpent));
    @Override
    public List<TimesheetEntry> forEmployee(final String userID, final int from) {
        return null;
    }

    @Override
    public List<TimesheetEntry> forYear(final int year, final int from) {
        return null;
    }

    /** For test. */
    void setLogger(final Logger logger) {
        this.logger = logger;
    }
}
