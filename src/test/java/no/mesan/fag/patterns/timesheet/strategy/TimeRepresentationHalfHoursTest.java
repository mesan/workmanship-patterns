package no.mesan.fag.patterns.timesheet.strategy;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tester for {@link TimeRepresentationHalfHours}
 */
public class TimeRepresentationHalfHoursTest {

    private final TimeRepresentationStrategy strategy = new TimeRepresentationHalfHours();

    @Test
    public void convertShouldConvertToHalfHours() {
        assertEquals(0.0, strategy.convert(0), 0.0);
        assertEquals(2.5, strategy.convert(150), 0.0);
    }

    @Test
    public void convertShouldTruncateExcessMinutes() {
        assertEquals(2.5, strategy.convert(155), 0.0);
    }
}
