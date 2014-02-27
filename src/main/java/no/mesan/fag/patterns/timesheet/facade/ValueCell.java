package no.mesan.fag.patterns.timesheet.facade;

import no.mesan.fag.patterns.timesheet.format.StyleFactory.StyleName;

/** En celle med verdiinnhold (ikke formel). */
abstract class ValueCell<T> extends SheetCell {

    /** Verdien. */
    private final T value;

    ValueCell(final T value, final StyleName style) {
        super(style);
        this.value= value;
    }

    T getValue() {
        return value;
    }
}
