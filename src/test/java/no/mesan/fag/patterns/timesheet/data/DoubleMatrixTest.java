package no.mesan.fag.patterns.timesheet.data;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test DoubleMatrix.
 */
public class DoubleMatrixTest extends ValueMatrixTest {

    @Override protected DoubleMatrix create() { return new DoubleMatrix();}

    @Test
    public void addToEmptyCellIsEqualToPut() {
        final DoubleMatrix m = create().add("C", "R", 5D);
        assertEquals(5D, m.get("C", "R"), 0.001D);
    }

    @Test
    public void addToNonEmptyCellAdds() {
        final DoubleMatrix m = create().add("C", "R", 5D).add("C", "R", 10D);
        assertEquals(15D, m.get("C", "R"), 0.001D);
        assertEquals(1, m.rSize());
    }
}
