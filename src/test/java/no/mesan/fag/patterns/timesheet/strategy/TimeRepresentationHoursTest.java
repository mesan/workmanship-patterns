package no.mesan.fag.patterns.timesheet.strategy;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tester for {@link TimeRepresentationHours}
 */
public class TimeRepresentationHoursTest {

    private final TimeRepresentationStrategy strategy = new TimeRepresentationHours();

    @Test
    public void convertShouldConvertToWholeHours() {
        assertEquals(0.0, strategy.convert(0), 0.0);
        assertEquals(2.0, strategy.convert(149), 0.0);
        assertEquals(3.0, strategy.convert(150), 0.0);
        assertEquals(3.0, strategy.convert(209), 0.0);
        assertEquals(8.0, strategy.convert(450), 0.0);
    }
}
