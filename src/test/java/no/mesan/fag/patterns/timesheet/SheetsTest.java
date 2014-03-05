package no.mesan.fag.patterns.timesheet;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.SmallTimeSource;
import no.mesan.fag.patterns.timesheet.external.TimeDataServer;
import no.mesan.fag.patterns.timesheet.strategy.TimeRepresentationStrategy;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/** Tester for {@link Sheets} */
public class SheetsTest {

    @Test
    public void minutesToHoursShouldDelegateToGivenStrategy() {
        final TimeRepresentationStrategy mockStrategy = mock(TimeRepresentationStrategy.class);
        when(mockStrategy.convert(anyInt())).thenReturn(42.0);
        final Sheets sheets = new Aarsliste(2014, new TimeDataServer(new SmallTimeSource()));
        sheets.setTimeRepresentationStrategy(mockStrategy);
        final TimesheetEntry timeEntry = TimesheetEntry.create("osk", "2014-07-30", "1030", "150").get(0);

        final double timeSpent = sheets.minutesToCorrectRepresentation(timeEntry);

        verify(mockStrategy).convert(150);
        assertEquals(42.0, timeSpent, 0.0);
    }
}
