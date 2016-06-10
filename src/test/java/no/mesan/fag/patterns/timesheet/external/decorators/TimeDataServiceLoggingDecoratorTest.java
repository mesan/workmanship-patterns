package no.mesan.fag.patterns.timesheet.external.decorators;

import java.util.List;
import java.util.logging.Logger;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.TimeDataService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

/** Tester for {@link TimeDataServiceLoggingDecorator}. */
public class TimeDataServiceLoggingDecoratorTest {

    private TimeDataServiceLoggingDecorator loggingDecorator;
    private Logger loggerMock;

    @Before
    public void setUp() {
        final List<TimesheetEntry> timesheetEntries = TimesheetEntry.create("osk", "2014-07-30", "1030", "60",
                                                                            "osk", "2014-07-31", "1030", "60");
        final TimeDataService timeDataServiceMock = mock(TimeDataService.class);

        // Dette er kanskje litt unødvendig fancy for unit-tester for en fagkveld,
        // men for å mocke ut noe hvor bi-effekter som tid brukt er vel så relevant som resultatet
        // så er dette et hendig triks. Ellers ville vel den følgende one-lineren vært greiere å bruke:
        // when(timeDataServiceMock.forEmployee(anyString(), anyInt())).thenReturn(timesheetEntries);
        final Answer<List<TimesheetEntry>> webserviceAnswer = invocation -> {
            Thread.sleep(42); // simulere at webservicen tar litt tid
            return timesheetEntries;
        };
        doAnswer(webserviceAnswer).when(timeDataServiceMock).forEmployee(anyString(), anyInt());
        doAnswer(webserviceAnswer).when(timeDataServiceMock).forYear(anyInt(), anyInt());
        when(timeDataServiceMock.forEmployee(anyString(), anyInt())).thenReturn(timesheetEntries);

        loggingDecorator = new TimeDataServiceLoggingDecorator(timeDataServiceMock);
        loggerMock= mock(Logger.class);
        loggingDecorator.setLogger(loggerMock);
    }

    @Test
    @Ignore /// HINT Denne testen bør virke
    public void forEmployeeShouldLog() {
        loggingDecorator.forEmployee("osk", 0);
        verify(loggerMock, times(2)).info(anyString());
    }

    @Test
    @Ignore /// HINT Denne testen bør virke
    public void forYearShouldLog() {
        loggingDecorator.forYear(2014, 0);
        verify(loggerMock, times(2)).info(anyString());
    }
}
