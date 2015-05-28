package no.mesan.fag.patterns.timesheet.external.decorators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.TimeDataService;
import org.joda.time.LocalDate;

/** Dekoratør for vår eksterne tjeneste som sørger for at vi kun slår opp gamle data en gang. */
public class TimeDataServiceCachingDecorator extends TimeDataServiceDecorator {

    private static final Logger LOG = Logger.getLogger("TimeDataServiceCachingDecorator");

    private final Map<YearFromPair, List<TimesheetEntry>> cache = new HashMap<>();

    public TimeDataServiceCachingDecorator(final TimeDataService timeDataService) {
        super(timeDataService);
    }

    @Override // Intet å gjøre her, delegerer bare videre
    public List<TimesheetEntry> forEmployee(final String userID, final int from) {
        return timeDataService.forEmployee(userID, from);
    }

    @Override
    public List<TimesheetEntry> forYear(final int year, final int from) {
        return (year >= new LocalDate().getYear())? timeDataService.forYear(year, from)
                                                  : cacheLookup(year, from);
    }

    private List<TimesheetEntry> cacheLookup(final int year, final int from) {
        final YearFromPair yearFromPair = new YearFromPair(year, from);
        return cache.computeIfAbsent(yearFromPair, yfp-> timeDataService.forYear(year, from));
    }

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
