package no.mesan.fag.patterns.timesheet.external;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test av iteratorimplementasjonen
 */
public class TimeIteratorServiceTest {

    @Test
    public void forEmployeeHandlesEmptyData() {
        final List<TimesheetEntry> dataList = new LinkedList<>();
        assertEquals(0, getForEmployee("lre", dataList).size());
        dataList.addAll(TimesheetEntry.create("nix", "2014-02-24", "1022", "100"));
        assertEquals(0, getForEmployee("lre", dataList).size());
    }

    @Test
    public void forEmployeeHandlesOne() {
        final List<TimesheetEntry> dataList = new LinkedList<>();
        dataList.addAll(TimesheetEntry.create("nix", "2014-02-24", "1022", "100",
                                              "lre", "2014-02-24", "1022", "100"));
        assertEquals(1, getForEmployee("lre", dataList).size());
    }

    @Test
    public void forEmployeeHandlesExactBatch() {
        final List<TimesheetEntry> dataList = new LinkedList<>();
        makeEntries(dataList, TimeDataServer.BATCH_SIZE);
        assertEquals(TimeDataServer.BATCH_SIZE, getForEmployee("lre", dataList).size());
    }

    @Test
    public void forEmployeeHandlesSeveralPages() {
        final List<TimesheetEntry> dataList = new LinkedList<>();
        makeEntries(dataList, 2*TimeDataServer.BATCH_SIZE+1);
        assertEquals(2*TimeDataServer.BATCH_SIZE+1, getForEmployee("lre", dataList).size());
    }

    @Test
    public void forYearHandlesEmptyData() {
        final List<TimesheetEntry> dataList = new LinkedList<>();
        assertEquals(0, getForYear(2014, dataList).size());
        dataList.addAll(TimesheetEntry.create("nix", "2013-02-24", "1022", "100"));
        assertEquals(0, getForYear(2014, dataList).size());
    }

    @Test
    public void forYearHandlesOne() {
        final List<TimesheetEntry> dataList = new LinkedList<>();
        dataList.addAll(TimesheetEntry.create("nix", "2014-02-24", "1022", "100",
                                              "lre", "2015-02-24", "1022", "100"));
        assertEquals(1, getForYear(2014, dataList).size());
    }

    @Test
    public void forYearHandlesExactBatch() {
        final List<TimesheetEntry> dataList = new LinkedList<>();
        makeEntries(dataList, TimeDataServer.BATCH_SIZE);
        assertEquals(TimeDataServer.BATCH_SIZE, getForYear(2014, dataList).size());
    }

    @Test
    public void forYearHandlesSeveralPages() {
        final List<TimesheetEntry> dataList = new LinkedList<>();
        makeEntries(dataList, 2*TimeDataServer.BATCH_SIZE+1);
        assertEquals(2*TimeDataServer.BATCH_SIZE+1, getForYear(2014, dataList).size());
    }

    private static void makeEntries(final List<TimesheetEntry> dataList, final int size) {
        for (int i = 0; i < size; i++) {
            dataList.addAll(TimesheetEntry.create("lre", "2014-02-24", String.format("%04d", 100 + i), "100"));
        }
    }

    private static List<TimesheetEntry> getForEmployee(final String userID, final List<TimesheetEntry> dataList) {
        final TimeIteratorService service = new TimeIteratorService(new TimeDataServer(dataList));
        return StreamSupport.stream(service.forEmployee(userID).spliterator(), false)
                       .collect(Collectors.toList());
    }

    private static List<TimesheetEntry> getForYear(final int year, final List<TimesheetEntry> dataList) {
        final TimeIteratorService service = new TimeIteratorService(new TimeDataServer(dataList));
        return StreamSupport.stream(service.forYear(year).spliterator(), false)
                       .collect(Collectors.toList());
    }
}
