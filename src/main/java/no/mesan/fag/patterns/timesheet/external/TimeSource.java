package no.mesan.fag.patterns.timesheet.external;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/** Kilde til timedata. */
public class TimeSource implements Iterable<TimesheetEntry> {

    private static List<String> readAllLines(final String fileName) {
        final List<String> res = new LinkedList<>();
        try {
            final List<String> lines = Files.readAllLines(new File(fileName).toPath(), Charset.defaultCharset());
            for (final String line : lines) {
                res.addAll(Arrays.asList(line.split(" +")));
            }
            return res;
        }
        catch (IOException e) {
            e.printStackTrace();
            return res;
        }
    }

    private static final List<TimesheetEntry> ENTRIES=
            TimesheetEntry.create(readAllLines("src/main/resources/timer.txt"));

    @Override
    public Iterator<TimesheetEntry> iterator() {
        return ENTRIES.iterator();
    }
}
