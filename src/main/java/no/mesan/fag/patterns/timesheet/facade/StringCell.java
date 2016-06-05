package no.mesan.fag.patterns.timesheet.facade;

import no.mesan.fag.patterns.timesheet.format.StyleFactory.StyleName;

/** En celle med tekstinnhold. */
public class StringCell extends ValueCell<String> {
    public StringCell(final String value, final StyleName style) {
        super(value, style);
    }
    /// HINT @Override protected Cell fillCell(final Cell cell)
}
