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
    public List<TimesheetEntry> forEmployee(final String userID, final int from) {
        final LinkedList<TimesheetEntry> list = new LinkedList<>();
        int skip= from;
        for (final TimesheetEntry entry : source) {
            if (entry.getUserID().equals(userID)) {
                if (skip-- <=0) list.add(entry);
            }
        }
        if (list.size()>BATCH_SIZE) return list.subList(0, BATCH_SIZE);
        return list;
    }

    @Override
    public List<TimesheetEntry> forYear(final int year, final int from) {
        final LinkedList<TimesheetEntry> list = new LinkedList<>();
        int skip= from;
        for (final TimesheetEntry entry : source) {
            final LocalDate when = entry.getWhen();
            if (when.year().get() == year) {
                if (skip-- <=0) list.add(entry);
            }
        }
        if (list.size()>BATCH_SIZE) return list.subList(0, BATCH_SIZE);
        return list;
    }
}
