package no.mesan.fag.patterns.timesheet.external;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import org.joda.time.LocalDate;

import java.util.LinkedList;
import java.util.List;

/**
 * Implementasjon av TimeDataService.
 */
public class TimeDataServer implements TimeDataService {

    private final Iterable<TimesheetEntry> source;

    /** Default constructor. */
    public TimeDataServer(final Iterable<TimesheetEntry> src) {
        this.source = src;
    }

    @Override
    public List<TimesheetEntry> forEmployee(final String userID) {
        final LinkedList<TimesheetEntry> list = new LinkedList<>();
        for (final TimesheetEntry entry : source) {
            if (entry.getUserID().equals(userID)) list.add(entry);
        }
        return list;
    }

    @Override
    public List<TimesheetEntry> forYear(final int year) {
        final LinkedList<TimesheetEntry> list = new LinkedList<>();
        for (final TimesheetEntry entry : source) {
            final LocalDate when = entry.getWhen();
            if (when.year().get() == year) list.add(entry);
        }
        return list;
    }
}
