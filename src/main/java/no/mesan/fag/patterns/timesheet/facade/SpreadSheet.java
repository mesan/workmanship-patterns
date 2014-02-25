package no.mesan.fag.patterns.timesheet.facade;

import no.mesan.fag.patterns.timesheet.data.ValueMatrix;

/**
 * Holder data om et regneark uten å blande inn POI før til slutt.
 */
public class SpreadSheet {

    /** Navnet på (det eneste) arket. */
    private final String name;

    /** Innholdet i arket. */
    private final ValueMatrix<Integer, Integer, SheetCell> data=new ValueMatrix<>();

    public SpreadSheet(final String name) {
        this.name = name;
    }
}
