package no.mesan.fag.patterns.timesheet.strategy;

/** Konverterer minutter til hele dager. */
public class TimeRepresentationDays implements TimeRepresentationStrategy {

    @Override
    public double convert(final int minutes) {
        return minutes / (60 * 7.5D);
    }
}
