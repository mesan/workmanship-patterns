package no.mesan.fag.patterns.timesheet.facade;

import no.mesan.fag.patterns.timesheet.format.StyleFactory.StyleName;
import org.apache.poi.ss.usermodel.Cell;

/** En celle uten innhold. */
public class EmptyCell extends SheetCell {
    Cell slettMeg; /// HINT ta bort denne, er her bare for å ha import på riktig Cell...
    public EmptyCell(final StyleName style) { super(style); }
    /// HINT @Override protected Cell fillCell(final Cell cell)
}
