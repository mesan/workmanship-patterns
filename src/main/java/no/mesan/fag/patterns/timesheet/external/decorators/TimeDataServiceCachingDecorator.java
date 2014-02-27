package no.mesan.fag.patterns.timesheet.external.decorators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.TimeDataService;

import org.joda.time.LocalDate;

/**
 * Dekoratør for vår eksterne tjeneste som sørger for at vi kun slår opp gamle data en gang.
 */
public class TimeDataServiceCachingDecorator extends TimeDataServiceDecorator {

    private static final Logger LOG = Logger.getLogger("TimeDataServiceCachingDecorator");

    private final Map<YearFromPair, List<TimesheetEntry>> cache = new HashMap<>();

    public TimeDataServiceCachingDecorator(final TimeDataService timeDataService) {
        super(timeDataService);
    }

    @Override
    public List<TimesheetEntry> forEmployee(final String userID, final int from) {
        // Intet å gjøre her, delegerer bare videre
        return timeDataService.forEmployee(userID, from);
    }

    @Override
    public List<TimesheetEntry> forYear(final int year, final int from) {
        if (year >= new LocalDate().getYear()) {
            return timeDataService.forYear(year, from);
        }

        // alright, sjekk om vi har cachet den alt
        final YearFromPair yearFromPair = new YearFromPair(year, from);
        final List<TimesheetEntry> cachedEntries = cache.get(yearFromPair);
        if (cachedEntries != null) {
            LOG.info(String.format("Cache hit, not fetching for year=%d, from=%d", year, from));
            return cachedEntries;
        } else {
            final List<TimesheetEntry> fetchedEntries = timeDataService.forYear(year, from);
            cache.put(yearFromPair, fetchedEntries);
            return fetchedEntries;
        }
    }

    private class YearFromPair {

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

            if (year != that.year) return false;
            if (from != that.from) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hash(year, from);
        }
    }
}
