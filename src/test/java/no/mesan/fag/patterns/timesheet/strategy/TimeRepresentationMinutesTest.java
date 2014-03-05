package no.mesan.fag.patterns.timesheet.strategy;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tester for {@link TimeRepresentationMinutes}
 */
public class TimeRepresentationMinutesTest {

    private final TimeRepresentationStrategy strategy = new TimeRepresentationMinutes();

    @Test
    public void convertShouldReturnInput() {
        assertEquals(0.0, strategy.convert(0), 0.0);
        assertEquals(42.0, strategy.convert(42), 0.0);
    }
}
