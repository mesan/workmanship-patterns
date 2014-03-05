package no.mesan.fag.patterns.timesheet.strategy;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tester for {@link TimeRepresentationHours}
 */
public class TimeRepresentationHoursTest {

    private final TimeRepresentationStrategy strategy = new TimeRepresentationHours();

    @Test
    public void convertShouldConvertToWholeHours() {
        assertEquals(0.0, strategy.convert(0), 0.0);
        assertEquals(3.0, strategy.convert(180), 0.0);
        assertEquals(7.5, strategy.convert(450), 0.0);
    }
}
