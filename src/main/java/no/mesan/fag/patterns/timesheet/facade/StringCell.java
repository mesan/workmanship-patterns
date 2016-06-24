package no.mesan.fag.patterns.timesheet.facade;

import no.mesan.fag.patterns.timesheet.format.StyleFactory.StyleName;
import org.apache.poi.ss.usermodel.Cell;

/** En celle med tekstinnhold. */
public class StringCell extends ValueCell<String> {
    Cell slettMeg; /// HINT ta bort denne, er her bare for å ha import på riktig Cell...
    public StringCell(final String value, final StyleName style) {
        super(value, style);
    }
    /// HINT @Override protected Cell fillCell(final Cell cell)
}
