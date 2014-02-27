package no.mesan.fag.patterns.timesheet.facade;

import no.mesan.fag.patterns.timesheet.format.StyleFactory.StyleName;

/** Celle som inneholder en formel. */
public class FormulaCell extends SheetCell {

    /** Formelen som skal inn i cellen. */
    private final String formula;

    public FormulaCell(final String formula, final StyleName style) {
        super(style);
        this.formula = formula;
    }
}
