package no.mesan.fag.patterns.timesheet.facade;

import no.mesan.fag.patterns.timesheet.format.StyleFactory;

/**
 * En celle med verdiinnhold (ikkke formel).
 */
public class ValueCell<T> extends SheetCell {

    /** Verdien som skal inn. */
    private final T value;

    public ValueCell(final T value, final StyleFactory.StyleName style) {
        super(style);
        this.value = value;
    }
}
