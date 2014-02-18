package no.mesan.fag.patterns.timesheet.data;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test TimesheetEntry.
 */
public class TimesheetEntryTest {

    @Test
    public void shouldCreateEntriesFromStrings()  {
        final List<TimesheetEntry> entries = TimesheetEntry.create(
                "larsr", "2014-01-06", "1000", "30",
                "larsr", "2014-01-06", "2134", "420",
                "larsr", "2014-01-07", "2134", "540",
                "larsr", "2014-01-07", "1001", "30",
                "larsr", "2014-01-08", "2134", "450"
        );
        assertEquals(5, entries.size());
        for (final TimesheetEntry entry : entries)  {
            assertEquals("larsr", entry.getUserID());
            assertEquals(2014, entry.getWhen().year().get());
        }
        assertEquals(2134, entries.get(2).getActivity());
    }
}
