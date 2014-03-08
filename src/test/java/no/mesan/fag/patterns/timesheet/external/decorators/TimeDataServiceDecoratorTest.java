package no.mesan.fag.patterns.timesheet.external.decorators;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.TimeDataServer;
import no.mesan.fag.patterns.timesheet.external.TimeDataService;
import no.mesan.fag.patterns.timesheet.external.TimeIteratorService;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;

/** Tester for kombinering av dekoratører. */
public class TimeDataServiceDecoratorTest {

    private TimeDataServer timeDataServer;

    @Before
    public void setUp() {
        final List<TimesheetEntry> entries = TimesheetEntry.create("osk", "2013-07-30", "1030", "60");
        timeDataServer = new TimeDataServer(entries);
    }

    @Test
    public void shouldBePossibleToCombineDecorators() {
        final TimeDataService cachingDecoratedService =
            new TimeDataServiceCachingDecorator(timeDataServer);

        final TimeDataService loggingAndCachingDecoratedService =
            new TimeDataServiceLoggingDecorator(cachingDecoratedService);

        final TimeIteratorService timeIteratorService = new TimeIteratorService(loggingAndCachingDecoratedService);

        for (final TimesheetEntry timesheetEntry : timeIteratorService.forYear(2013)) {
            assertNotNull(timesheetEntry);
        }

        for (final TimesheetEntry timesheetEntry : timeIteratorService.forYear(2013)) {
            // Merk cache-treffene i tillegg til logging i runde to
            assertNotNull(timesheetEntry);
        }
    }
}
