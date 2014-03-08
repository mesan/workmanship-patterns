package no.mesan.fag.patterns.timesheet.external;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;

import java.util.Iterator;
import java.util.List;

/** Kilde til timedata. */
public class SmallTimeSource implements Iterable<TimesheetEntry> {
    private static final List<TimesheetEntry> ENTRIES= TimesheetEntry.create(
             "A","2014-01-01","1000","300",
             "A","2014-01-01","1000","150",
             "A","2014-01-02","1000","120",
             "A","2014-01-02","1001","60",
             "B","2014-01-02","1001","60",
             "C","2014-02-01","1000","600"
            );

    @Override
    public Iterator<TimesheetEntry> iterator() {
        return ENTRIES.iterator();
    }
}
