package no.mesan.fag.patterns.timesheet.strategy;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tester for {@link TimeRepresentationDays}
 */
public class TimeRepresentationDaysTest {

    private final TimeRepresentationStrategy strategy = new TimeRepresentationDays();

    @Test
    public void convertShouldConvertToDays() {
        assertEquals(0.0, strategy.convert(0), 0.0);
        assertEquals(1.0, strategy.convert(60 * 24), 0.0);
        assertEquals(1.5, strategy.convert(60 * 36), 0.0);
    }
}
