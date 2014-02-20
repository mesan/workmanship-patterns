package no.mesan.fag.patterns.timesheet.data;

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
 * C: The column type
 * R: The row type
 * V: The value types to keep
 * @author lre
 */
public class ValueMatrix<C extends Comparable, R extends Comparable, V> {

    /** Used to separate row & column in the storage. Inspired by AWK. */
    private static final String ARRAYSEP = "" + (char) 1;

    /** Keep track of row keys. */
    private final Set<R> allRowKeys = new LinkedHashSet<>();
    /** Keep track of column keys. */
    private final Set<C> allColKeys = new LinkedHashSet<>();
    /** The real values. */
    private final Map<String, V> values = new HashMap<>();

    /** Default constructor. */
    public ValueMatrix() {
        super();
    }

    /** Create map key. */
    private String key(final C col, final R row) {
        return col.toString() + ARRAYSEP + row.toString();
    }

    /** Create a possibly sorted list from a key set. */
    private static <T extends Comparable> List<T> keyList(final Boolean sorted, final Set<T> orgKeys) {
        final List<T> keys = new LinkedList<>(orgKeys);
        if (sorted) Collections.sort(keys);
        return keys;
    }

    /** Return list of row keys, possibly sorted. */
    public List<R> rowKeys(final Boolean sorted) {
        return keyList(sorted, this.allRowKeys);
    }

    /** Return list of column keys, possibly sorted. */
    public List<C> colKeys(final boolean sorted) {
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
    public V get(final C col, final R row) {
        return this.values.get(key(col, row));
    }

    /** Add a value, overwriting whatever was there. */
    public ValueMatrix<C, R, V> put(final C col, final R row, final V val) {
        this.allColKeys.add(col);
        this.allRowKeys.add(row);
        this.values.put(key(col, row), val);
        return this;
    }

    /** Get the value of a given cell (null if not defined). */
    public boolean has(final C col, final R row) {
        return get(col, row) != null;
    }

    /** Sometimes you need certain rows, regardless of whether they actually exist in data. */
    @SafeVarargs
    public final ValueMatrix<C, R, V> ensureRow(final R... row) {
        Collections.addAll(this.allRowKeys, row);
        return this;
    }

    /** Sometimes you need certain columns, regardless of whether they actually exist in data. */
    @SafeVarargs
    public final ValueMatrix<C, R, V> ensureCol(final C... col) {
        Collections.addAll(this.allColKeys, col);
        return this;
    }
}
