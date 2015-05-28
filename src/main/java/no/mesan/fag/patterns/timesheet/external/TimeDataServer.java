package no.mesan.fag.patterns.timesheet.external;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;

/** * Dette er liksom selve den eksterne serveren. Tilgi meg, hadde ikke råd til en stormaskin til hver. */
public class TimeDataServer implements TimeDataService {

    private final Iterable<TimesheetEntry> source;

    /** Default constructor. */
    public TimeDataServer(final Iterable<TimesheetEntry> src) {
        this.source = src;
    }


    @Override
    public List<TimesheetEntry> forEmployee(final String userID, final int from) {
        final List<TimesheetEntry> list =  StreamSupport.stream(source.spliterator(), false)
                .filter(entry -> entry.getUserID().equals(userID))
                .skip(from)
                .collect(Collectors.toList());
        return (list.size()>BATCH_SIZE)? list.subList(0, BATCH_SIZE) : list;
    }

    @Override
    public List<TimesheetEntry> forYear(final int year, final int from) {
        final List<TimesheetEntry> list =  StreamSupport.stream(source.spliterator(), false)
                .filter(entry -> entry.getWhen().year().get() == year)
                .skip(from)
                .collect(Collectors.toList());
        return (list.size()>BATCH_SIZE)? list.subList(0, BATCH_SIZE) : list;
    }
}
