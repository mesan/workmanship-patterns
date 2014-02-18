package no.mesan.fag.patterns.timesheet.external;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;

import java.util.LinkedList;
import java.util.List;

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
        final LinkedList<TimesheetEntry> list = new LinkedList<>();
        for (final TimesheetEntry entry : source) {
            if (entry.getUserID().equals(userID)) list.add(entry);
        }
        return list;
    }
}
