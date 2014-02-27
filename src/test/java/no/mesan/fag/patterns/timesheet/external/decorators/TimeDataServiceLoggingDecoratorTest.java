package no.mesan.fag.patterns.timesheet.external.decorators;

import static org.mockito.Mockito.*;

import java.util.List;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.TimeDataService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Tester for {@link TimeDataServiceLoggingDecorator}.
 */
public class TimeDataServiceLoggingDecoratorTest {

    private TimeDataServiceLoggingDecorator loggingDecorator;

    @Before
    public void setUp() {
        final List<TimesheetEntry> timesheetEntries = TimesheetEntry.create("osk", "2014-07-30", "1030", "60",
                                                                            "osk", "2014-07-31", "1030", "60");
        final TimeDataService timeDataServiceMock = mock(TimeDataService.class);

        // Dette er kanskje litt unødvendig fancy for unit-tester for en fagkveld,
        // men for å mocke ut noe hvor bi-effekter som tid brukt er vel så relevant som resultatet
        // så er dette et hendig triks. Ellers ville vel den følgende one-lineren vært greiere å bruke:
        // when(timeDataServiceMock.forEmployee(anyString(), anyInt())).thenReturn(timesheetEntries);
        final Answer webserviceAnswer = new Answer() {
            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                Thread.sleep(42); // simulere at webservicen tar litt tid
                return timesheetEntries;
            }
        };
        doAnswer(webserviceAnswer).when(timeDataServiceMock).forEmployee(anyString(), anyInt());
        doAnswer(webserviceAnswer).when(timeDataServiceMock).forYear(anyInt(), anyInt());
        when(timeDataServiceMock.forEmployee(anyString(), anyInt())).thenReturn(timesheetEntries);

        loggingDecorator = new TimeDataServiceLoggingDecorator(timeDataServiceMock);
    }

    @Test
    public void forEmployeeShouldLog() {
        // kikk i konsollet og se på output
        loggingDecorator.forEmployee("osk", 0);
    }

    @Test
    public void forYearShouldLog() {
        // kikk i konsollet og se på output
        loggingDecorator.forYear(2014, 0);
    }
}
