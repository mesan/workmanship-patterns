package no.mesan.fag.patterns.timesheet;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test ValueMatrix.
 */
public class ValueMatrixTest {

    protected ValueMatrix<Double> create() { return new ValueMatrix<>();}

    @Test
    public void emptyMatrixHasNoKeys()  {
        final ValueMatrix<Double> m = create();
        assertEquals(m.rowKeys(false).size(), 0);
        assertEquals(m.colKeys(false).size(),  0);
        assertEquals(m.rSize(), 0);
        assertEquals(m.cSize(), 0);
        assertNull(m.get("A", "1"));
    }

    @Test
    public void oneInMatrixFoundByRightKey()  {
        final ValueMatrix<Double> m = create().put("A", "1", 4.0D);
        assertEquals(m.rowKeys(false).size(), 1);
        assertEquals(m.colKeys(false).size(), 1);
        assertEquals(m.rSize(), 1);
        assertEquals(m.cSize(), 1);
        assertEquals(m.get("A", "1").doubleValue(), 4.0D, 0.0001D);
        assertNull(m.get("A", "2"));
    }

    @Test
    public void putOverwritesOldValue()  {
        final ValueMatrix<Double> m = create().put("A", "1", 4.0D);
        m.put("A", "2", 0.0D);
        m.put("A", "1", -10.0D);
        assertTrue(m.colKeys(false).contains("A"));
        assertTrue(m.rowKeys(false).contains("1"));
        assertEquals(m.rSize(), 2);
        assertEquals(m.cSize(), 1);
        assertEquals(m.get("A", "1").doubleValue(), -10.0D, 0.0001D);
    }

    @Test
    public void ensureRowsDoesntOverwriteValues()  {
        final ValueMatrix<Double> m = create().put("A", "1", 4.0D);
        m.ensureRow("1");
        assertTrue(m.rowKeys(false).contains("1"));
        assertEquals(m.rSize(), 1);
    }

    @Test
    public void ensureColsDoesntOverwriteValues()  {
        final ValueMatrix<Double> m = create().put("A", "1", 4.0D);
        m.ensureCol("A");
        assertTrue(m.colKeys(false).contains("A"));
        assertEquals(m.cSize(), 1);
    }

    @Test
    public void correctDimensionWithMultipleKeys()  {
        final ValueMatrix<Double> m = create56();
        for (int r=1; r<=5; r++) assertTrue(m.rowKeys(false).contains("R"+2*r));
        for (int c=1; c<=5; c++) assertTrue(m.colKeys(false).contains("C" + c));
        assertEquals(m.rSize(), 5);
        assertEquals(m.cSize(), 6);
        assertEquals(m.get("C3", "R4").doubleValue(), 24.0D, 0.0001D);
        assertNull(m.get("C1", "R5"));
    }

    @Test
    public void sortedKeys()  {
        final ValueMatrix<Double> m = create56();
        final List<String> res= new LinkedList<>();
        res.add("R10");
        for (int r=1; r<=4; r++) res.add("R" + (2*r));
        assertEquals(res, m.rowKeys(true));
        res.clear();
        for (int c=1; c<=6; c++) res.add("C" + c);
        assertEquals(res, m.colKeys(true));
    }

    private ValueMatrix<Double> create56() {
        final ValueMatrix<Double> m = create();
        for (int r=5; r>=1; r--)
            for (int c=6; c>=1; c--)
                m.put("C" + c, "R" + (2*r), 4.0D * r * c);
        return m;
    }
}
