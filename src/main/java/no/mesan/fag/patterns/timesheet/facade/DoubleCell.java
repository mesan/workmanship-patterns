package no.mesan.fag.patterns.timesheet.facade;

import no.mesan.fag.patterns.timesheet.format.StyleFactory.StyleName;

import org.apache.poi.ss.usermodel.Cell;

/** En celle med tallinnhold. */
public class DoubleCell extends ValueCell<Double> {
    public DoubleCell(final Double value, final StyleName style) {
        super(value, style);
    }

    @Override
    protected Cell fillCell(final Cell cell) {
        cell.setCellValue(getValue());
        return cell;
    }
}
