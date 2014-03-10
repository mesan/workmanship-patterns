package no.mesan.fag.patterns.timesheet.strategy;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/** Tester for {@link TimeRepresentationDays}. */
public class TimeRepresentationDaysTest {

    private final TimeRepresentationStrategy strategy = new TimeRepresentationDays();

    @Test
    public void convertShouldConvertToDays() {
        assertEquals(0.0, strategy.convert(0), 0.0);
        assertEquals(1.0, strategy.convert((int) (60 * 7.5)), 0.0);
        assertEquals(1.5, strategy.convert((int) (60 * 7.5 * 1.5)), 0.0);
    }
}
