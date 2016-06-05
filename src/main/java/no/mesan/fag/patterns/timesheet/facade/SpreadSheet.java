package no.mesan.fag.patterns.timesheet.facade;

import java.util.HashMap;
import java.util.Map;

import no.mesan.fag.patterns.timesheet.data.ValueMatrix;

/** Holder data om et regneark uten å blande inn POI før til slutt. */
public class SpreadSheet {

    /** Navnet på (det eneste) arket. */
    private final String name;

    /** Noen rader skal ha tilpasset høyde. */
    private final Map<Integer, Integer> rowHeigths = new HashMap<>();

    /** Innholdet i arket. */
    private final ValueMatrix<Integer, Integer, SheetCell> data = new ValueMatrix<>();

    /**
     * Default constructor.
     * @param name Navn på ark1 (og default på arbeidsboken)
     */
    public SpreadSheet(final String name) {
        this.name = name;
    }

    /**
     * Sett radhøyde.
     * @param rowNo Hvilken rad
     * @param points Høyde i punkter
     * @return this
     */
    public SpreadSheet setRowHeight(final int rowNo, final int points) {
        rowHeigths.put(rowNo, points);
        return this;
    }

    /**
     * Hent høyde på rad.
     * @param rowNo Radnr.
     * @return Høyde i punkter, eller null hvis default
     */
    Integer getRowHeight(final int rowNo) {
        return rowHeigths.get(rowNo);
    }

    /**
     * Sett inn celle.
     * @param colNum Kolonne
     * @param rowNum Rad
     * @param cell Celle
     */
    public void setCell(final int colNum, final int rowNum, final SheetCell cell) {
        data.put(colNum, rowNum, cell);
    }

    /**
     * Hent siste radnummer.
     * @return Radnummer
     */
    public int lastRowNum() {
        return data.rSize();
    }

    /**
     * Hent datainnholdet.
     * @return Data
     */
    public ValueMatrix<Integer, Integer, SheetCell> getData() {
        return data;
    }
}
