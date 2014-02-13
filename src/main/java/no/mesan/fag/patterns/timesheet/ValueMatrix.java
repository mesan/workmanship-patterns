package no.mesan.fag.patterns.timesheet;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A sort of sparse, associative, two-dimensional array.
 *
 * T: The value types to keep
 * @author lre
 */
public class ValueMatrix<T> {

    /** Used to separate row & column in the storage. Inspired by AWK. */
    private static final String ARRAYSEP = "" + (char) 1;

    /** Keep track of row keys. */
    private final Set<String> allRowKeys = new LinkedHashSet<>();
    /** Keep track of column keys. */
    private final Set<String> allColKeys = new LinkedHashSet<>();
    /** The real values. */
    private final Map<String, T> values = new HashMap<>();

    /** Default constructor. */
    public ValueMatrix() {
        super();
    }

    /** Create map key. */
    private static String key(final String col, final String row) {
        return col + ARRAYSEP + row;
    }

    /** Create a possibly sorted list from a key set. */
    private List<String> keyList(final Boolean sorted, final Set<String> orgKeys) {
        final List<String> keys = new LinkedList<>(orgKeys);
        if (sorted) Collections.sort(keys);
        return keys;
    }

    /** Return list of row keys, possibly sorted. */
    public List<String> rowKeys(final Boolean sorted) {
        return keyList(sorted, this.allRowKeys);
    }

    /** Return list of column keys, possibly sorted. */
    public List<String> colKeys(final boolean sorted) {
        return keyList(sorted, this.allColKeys);
    }

    /** Get the number of rows. */
    public int rSize() {
        return this.allRowKeys.size();
    }

    /** Get the number of columns. */
    public int cSize() {
        return this.allColKeys.size();
    }

    /** Get the value of a given cell (null if not defined). */
    public T get(final String col, final String row) {
        return this.values.get(key(col, row));
    }

    /** Add a value, overwriting whatever was there. */
    public ValueMatrix<T> put(final String col, final String row, final T val) {
        this.allColKeys.add(col);
        this.allRowKeys.add(row);
        this.values.put(key(col, row), val);
        return this;
    }

    /** Sometimes you need certain rows, regardless of whether they actually exist in data. */
    public ValueMatrix<T> ensureRow(final String... row) {
        for (String s : row) this.allRowKeys.add(s);
        return this;
    }

    /** Sometimes you need certain columns, regardless of whether they actually exist in data. */
    public ValueMatrix<T> ensureCol(final String... col) {
        for (String s : col) this.allColKeys.add(s);
        return this;
    }
}
