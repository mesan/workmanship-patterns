package no.mesan.fag.patterns.timesheet.data;

/**
 * A sort of sparse, associative, two-dimensional array for Doubles.
 *
 * @author lre
 */
public class DoubleMatrix extends ValueMatrix<String, String, Double> {

    public DoubleMatrix() {
        super();
    }

    /**
     * Insert cell, or add to value if already present.
     * @param col Column
     * @param row Row
     * @param val Value to add/insert
     * @return this
     */
    public DoubleMatrix add(final String col, final String row, final Double val) {
        final Double old = get(col, row);
        put(col, row, (old == null) ? val : val + old);
        return this;
    }
}
