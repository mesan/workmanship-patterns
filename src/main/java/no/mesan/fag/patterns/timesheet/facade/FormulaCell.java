package no.mesan.fag.patterns.timesheet.facade;

import no.mesan.fag.patterns.timesheet.format.StyleFactory.StyleName;

/** Celle som inneholder en formel. */
public class FormulaCell extends SheetCell {

    /** Formelen som skal inn i cellen. */
    private final String formula;

    private FormulaCell(final String formula, final StyleName style) {
        super(style);
        this.formula = formula;
    }

    /** Opprett en SUM-celle. */
    public static FormulaCell formulaSUM(final int fromCol, final int fromRow, final int toCol, final int toRow,
                                         final StyleName style) {
        return new FormulaCell(String.format("SUM(%s)", rangeRef(fromCol, fromRow, toCol, toRow)), style);
    }

    @Override
    protected void visit(final CellVisitor visitor) {
        visitor.acceptFormula(this);
    }

    String getFormula() {
        return formula;
    }
}
