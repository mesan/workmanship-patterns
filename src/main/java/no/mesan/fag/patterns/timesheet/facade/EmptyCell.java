package no.mesan.fag.patterns.timesheet.facade;

import no.mesan.fag.patterns.timesheet.format.StyleFactory.StyleName;
import org.apache.poi.ss.usermodel.Cell;

/** En celle uten innhold. */
public class EmptyCell extends SheetCell {
    public EmptyCell(StyleName style) { super(style); }
    @Override protected Cell fillCell(final Cell cell) { return cell; }
}
