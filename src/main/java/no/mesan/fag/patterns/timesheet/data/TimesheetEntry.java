package no.mesan.fag.patterns.timesheet.data;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

/** 1 entry i timelisten. */
public class TimesheetEntry {
    private final String userID;
    private final LocalDate when;
    private final int activity;
    private final int minutes;

    private TimesheetEntry(final String userID, final LocalDate when, final int activity, final int minutes) {
        this.userID = userID;
        this.when = when;
        this.minutes = minutes;
        this.activity = activity;
    }

    /**
     * Lag 1 eller flere forekomster fra strenger.  Ikke for den svake av hjertet.
     * @param entries n*(userID,when,activity,minutes)
     * @return Innslag
     */
    public static List<TimesheetEntry> create(final String ... entries) {
        final List<TimesheetEntry> res= new ArrayList<>();
        for (int i=0; i<entries.length; i+=4) {
            res.add(new TimesheetEntry(entries[i],
                                       LocalDate.parse(entries[i + 1]),
                                       Integer.parseInt(entries[i + 2]),
                                       Integer.parseInt(entries[i + 3])));
        }
        return res;
    }

    /**
     * Lag 1 eller flere forekomster fra strenger.  Ikke for den svake av hjertet.
     * @param entries n*(userID,when,activity,minutes)
     * @return Innslag
     */
    public static List<TimesheetEntry> create(final List<String> entries) {
        return create(entries.toArray(new String[entries.size()]));
    }

    public String getUserID() {
        return userID;
    }

    public LocalDate getWhen() {
        return when;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getActivity() {
        return activity;
    }

    @Override
    public String toString() {
        return new StringBuilder("TimesheetEntry{")
          .append("userID='").append(userID).append('\'')
          .append(", when=").append(when)
          .append(", what=").append(activity)
          .append(", minutes=").append(minutes)
          .append('}')
          .toString();
    }
}
