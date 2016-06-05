package no.mesan.fag.patterns.timesheet.facade;

/** Visitor som kan besøke SheetCells. */
public interface CellVisitor {
    void acceptString(StringCell cell);
    void acceptFormula(FormulaCell cell);
    void acceptEmpty(EmptyCell cell);
    void acceptDouble(DoubleCell cell);
}
