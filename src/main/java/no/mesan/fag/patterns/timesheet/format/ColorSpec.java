package no.mesan.fag.patterns.timesheet.format;

import org.apache.poi.ss.usermodel.IndexedColors;

/** Holder sett av farger. */
public enum ColorSpec {
    STD_FG_COLOR(IndexedColors.BLACK),
    STD_BG_COLOR(IndexedColors.WHITE),
    STD_H_COLOR(IndexedColors.DARK_BLUE),
    SHADE_BG_COLOR(IndexedColors.DARK_BLUE),
    SHADE_FG_COLOR(IndexedColors.WHITE),
    DATA_GRID_COLOR(IndexedColors.BLACK);

    private final int color;

    ColorSpec(final IndexedColors color) {
        this.color= color.getIndex();
    }

    public int color() {
        return color;
    }
}
