package no.mesan.fag.patterns.timesheet.external.decorators;

import java.util.List;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.TimeDataService;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.mockito.Mockito.*;

/** Tester for {@link TimeDataServiceCachingDecorator}. */
public class TimeDataServiceCachingDecoratorTest {

    private TimeDataServiceCachingDecorator cachingDecorator;
    private TimeDataService timeDataServiceMock;

    @Before
    public void setUp() {
        timeDataServiceMock = mock(TimeDataService.class);
        cachingDecorator = new TimeDataServiceCachingDecorator(timeDataServiceMock);

        final List<TimesheetEntry> timesheetEntries = TimesheetEntry.create("osk", "2014-07-30", "1030", "60",
                                                                            "osk", "2014-07-31", "1030", "60");
        when(timeDataServiceMock.forYear(anyInt(), anyInt())).thenReturn(timesheetEntries);
    }

    @Test
    @Ignore /// HINT Denne testen bør virke
    public void forYearShouldFetchFromServiceEveryTimeForCurrentYear() {
        final int currentYear = new LocalDate().getYear();

        cachingDecorator.forYear(currentYear, 0);
        cachingDecorator.forYear(currentYear, 0);
        cachingDecorator.forYear(currentYear, 0);

        verify(timeDataServiceMock, times(3)).forYear(currentYear, 0);
    }

    @Test
    @Ignore /// HINT Denne testen bør virke
    public void forYearShouldOnlyFetchFromServiceTheFirstTimeForLastYear() {
        final int lastYear = new LocalDate().getYear() - 1;

        cachingDecorator.forYear(lastYear, 0);
        cachingDecorator.forYear(lastYear, 0);
        cachingDecorator.forYear(lastYear, 0);

        verify(timeDataServiceMock, times(1)).forYear(lastYear, 0);
    }
}
