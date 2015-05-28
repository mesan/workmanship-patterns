package no.mesan.fag.patterns.timesheet.external;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;

/**
 * Implementasjon av TimeDataService.
 */
public class TimeDataServer implements TimeDataService {

    private final TimeSource source;

    /** Default constructor. */
    public TimeDataServer() {
        this.source = new TimeSource();
    }

    @Override
    public List<TimesheetEntry> forEmployee(final String userID) {
        return StreamSupport.stream(source.spliterator(), false)
                .filter(entry -> entry.getUserID().equals(userID))
                .collect(Collectors.toList());
    }
}
