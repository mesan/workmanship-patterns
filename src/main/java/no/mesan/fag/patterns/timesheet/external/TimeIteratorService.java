package no.mesan.fag.patterns.timesheet.external;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

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

        @Override
        public boolean hasNext() {
            refillIfNeeded();
            return !buffer.isEmpty();
        }

        private void refillIfNeeded() {
            if (!buffer.isEmpty()  || lastIsFetched) return;
            final List<TimesheetEntry> newBatch = getNextFrom(fetched);
            fetched+= newBatch.size();
            buffer.addAll(newBatch);
            lastIsFetched= newBatch.isEmpty();
        }

        @Override
        public TimesheetEntry next() {
            refillIfNeeded();
            if (buffer.isEmpty()) throw new NoSuchElementException();
            final TimesheetEntry entry = buffer.get(0);
            buffer.remove(0);
            return entry;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Iterable<TimesheetEntry> forEmployee(final String userID) {
        return new Iterable<TimesheetEntry>() {
            @Override public Iterator<TimesheetEntry> iterator() {
                return new EntryIterator() {
                    @Override protected List<TimesheetEntry> getNextFrom(final int from) {
                        return original.forEmployee(userID, from);
                    }
                };
            }
        };
    }

    @Override
    public Iterable<TimesheetEntry> forYear(final int year) {
        return new Iterable<TimesheetEntry>() {
            @Override public Iterator<TimesheetEntry> iterator() {
                return new EntryIterator() {
                    @Override protected List<TimesheetEntry> getNextFrom(final int from) {
                        return original.forYear(year, from);
                    }
                };
            }
        };
    }
}
