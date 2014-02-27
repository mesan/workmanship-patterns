package no.mesan.fag.patterns.timesheet.facade;

import no.mesan.fag.patterns.timesheet.format.StyleFactory.StyleName;

/** Rotklasse for "ting som skal i celler". */
public abstract class SheetCell {
    /** Stilen som skal brukes. */
    private final StyleName style;

    protected SheetCell(final StyleName style) {
        this.style = style;
    }

    /**
     * Lag en cellereferanse (type A2) for en gitt kolonne+rad.
     * @param col Kolonne
     * @param row Rad
     * @return Celleref
     */
    public static String cellRef(final int col, final int row) {
        final int col0= col-1;
        final int ii= col0/26;
        final int i= col0%26;
        final StringBuilder b= new StringBuilder();
        if (ii>0) b.append((char) ('A'+ii-1));
        return b.append((char) ('A'+i))
                .append(row)
                .toString();
    }

    /**
     * Lag en referanse til et celleområde (range, type A2:B5) for en gitt kolonne+rad.
     * @param fromCol Fra kolonne
     * @param fromRow Fra rad
     * @param toCol Til kolonne
     * @param toRow Til rad
     * @return Referanse til range
     */
    public static String rangeRef(final int fromCol, final int fromRow, final int toCol, final int toRow) {
        return cellRef(fromCol, fromRow) + ":" + cellRef(toCol, toRow);
    }
}
