package no.mesan.fag.patterns.timesheet.strategy;

/** Gjør ingen konvertering, returnerer antall minutter uendret. */
public class TimeRepresentationMinutes implements TimeRepresentationStrategy {

    @Override
    public double convert(final int minutes) {
        return minutes;
    }
}
