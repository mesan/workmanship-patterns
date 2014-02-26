package no.mesan.fag.patterns.timesheet.facade;

import no.mesan.fag.patterns.timesheet.format.StyleFactory.StyleName;

import org.apache.poi.ss.usermodel.Cell;

/** En celle med tekstinnhold. */
public class StringCell extends ValueCell<String> {
    public StringCell(final String value, final StyleName style) {
        super(value, style);
    }

    protected Cell fillCell(final Cell cell) {
        cell.setCellValue(getValue());
        return cell;
    }
}
