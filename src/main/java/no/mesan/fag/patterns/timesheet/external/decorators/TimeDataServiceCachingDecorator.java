package no.mesan.fag.patterns.timesheet.external.decorators;

import java.util.List;
import java.util.Objects;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.TimeDataService;

/** Dekoratør for vår eksterne tjeneste som sørger for at vi kun slår opp gamle data en gang. */
public class TimeDataServiceCachingDecorator extends TimeDataServiceDecorator {

    /// HINT private final Map<...> cache = new HashMap<>();

    public TimeDataServiceCachingDecorator(final TimeDataService timeDataService) {
        super(timeDataService);
    }

    @Override // Intet å gjøre her, delegerer bare videre
    public List<TimesheetEntry> forEmployee(final String userID, final int from) {
        return timeDataService.forEmployee(userID, from);
    }

    @Override
    public List<TimesheetEntry> forYear(final int year, final int from) {
        /// HINT Her må du koble på en cache!
        return timeDataService.forYear(year, from);
    }

    /// HINT Kanskje du kan bruke denne?
    private static class YearFromPair {
        private final int year;
        private final int from;

        public YearFromPair(final int year, final int from) {
            this.year = year;
            this.from = from;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final YearFromPair that = (YearFromPair) o;
            return year == that.year && (from == that.from);
        }

        @Override
        public int hashCode() {
            return Objects.hash(year, from);
        }
    }
}
