package no.mesan.fag.patterns.timesheet.external;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;

/** Adapter TimeDataService til en TimeServiceAdapter. */
public class TimeIteratorService implements TimeServiceAdapter {
    private final TimeDataService original;

    public TimeIteratorService(final TimeDataService original) {
        this.original = original;
    }

    private abstract static class EntryIterator implements Iterator<TimesheetEntry> {
        /** Implementer denne for å hente data. */
        protected abstract List<TimesheetEntry> getNextFrom(final int from);

        // Holder de vi har hentet så langt
        private final List<TimesheetEntry> buffer= new ArrayList<>();

        // Antall vi har plukket ut
        int fetched= 0;

        // Har vi hentet alle?
        boolean lastIsFetched= false;

        private void refillIfNeeded() {
            if (!buffer.isEmpty() || lastIsFetched) return;
            final List<TimesheetEntry> newBatch = getNextFrom(fetched);
            fetched += newBatch.size();
            buffer.addAll(newBatch);
            lastIsFetched = newBatch.isEmpty();
        }

        /// HINT Du må lage hasNext() og next() for at dette skal virke...
        ///      datastrukturene og metoden over er ment å kunne hjelpe deg med dette

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Iterable<TimesheetEntry> forEmployee(final String userID) {
        return null; /// HINT returner en lambda basert på klassen over, omtrent slik:
                     ///    return () -> new EntryIterator() { ... }
    }

    @Override
    public Iterable<TimesheetEntry> forYear(final int year) {
        return null;  /// HINT ditto
    }
}
