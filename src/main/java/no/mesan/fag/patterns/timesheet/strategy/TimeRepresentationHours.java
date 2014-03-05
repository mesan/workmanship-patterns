package no.mesan.fag.patterns.timesheet.strategy;

/** Konverterer minutter til hele timer. */
public class TimeRepresentationHours implements TimeRepresentationStrategy {

    @Override
    public double convert(final int minutes) {
        return ((double) minutes / 60);
    }
}
