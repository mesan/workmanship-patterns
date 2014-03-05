package no.mesan.fag.patterns.timesheet.strategy;

/** Konverterer minutter til hele dager. */
public class TimeRepresentationDays implements TimeRepresentationStrategy {

    @Override
    public double convert(final int minutes) {
        return ((double) minutes / (60 * 24));
    }
}
