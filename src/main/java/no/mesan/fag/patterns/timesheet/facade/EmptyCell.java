package no.mesan.fag.patterns.timesheet.facade;

import no.mesan.fag.patterns.timesheet.format.StyleFactory.StyleName;

/** En celle uten innhold. */
public class EmptyCell extends SheetCell {
    public EmptyCell(final StyleName style) { super(style); }
    /// HINT @Override protected Cell fillCell(final Cell cell)
}
