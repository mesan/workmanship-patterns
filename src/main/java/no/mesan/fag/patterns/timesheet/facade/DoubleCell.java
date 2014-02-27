package no.mesan.fag.patterns.timesheet.facade;

import no.mesan.fag.patterns.timesheet.format.StyleFactory.StyleName;

/** En celle med tallinnhold. */
public class DoubleCell extends ValueCell<Double> {
    public DoubleCell(final Double value, final StyleName style) {
        super(value, style);
    }
}
