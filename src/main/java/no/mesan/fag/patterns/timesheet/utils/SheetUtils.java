package no.mesan.fag.patterns.timesheet.utils;

/**
 * Hjelperutiner for regneark.
 */
public final class SheetUtils {

    /**
     * Lag en cellereferanse (type A2B5) for en gitt kolonne+rad.
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
}
