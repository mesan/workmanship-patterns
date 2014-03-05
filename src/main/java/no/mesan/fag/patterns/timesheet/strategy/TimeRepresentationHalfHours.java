package no.mesan.fag.patterns.timesheet.strategy;

/** Konverterer minutter til halvtimer. */
public class TimeRepresentationHalfHours implements TimeRepresentationStrategy {

    @Override
    public double convert(final int minutes) {
        return (minutes / 30) / 2.0;
    }
}
