package no.mesan.fag.patterns.timesheet.strategy;

/** Konverterer minutter til halvtimer. */
public class TimeRepresentationHalfHours implements TimeRepresentationStrategy {

    /// HINT Dette er originalalgoritmen.
    ///    Lag tilsvarende for hele dager, hele timer og originalverdien i minutter - husk tester

    @Override
    public double convert(final int minutes) {
        return (minutes / 30) / 2.0;
    }
}
