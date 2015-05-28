package no.mesan.fag.patterns.timesheet.format;

import org.apache.poi.ss.usermodel.IndexedColors;

/** Holder sett av farger. */
public enum ColorSpec {
    STD_FG_COLOR(0),
    STD_BG_COLOR(1),
    STD_H_COLOR(2),
    SHADE_BG_COLOR(3),
    SHADE_FG_COLOR(4),
    DATA_GRID_COLOR(5);

    public enum Theme { RED, GREEN, BLUE }

    private static final int[] BLUE_THEME= {
        IndexedColors.BLACK.getIndex(),
        IndexedColors.WHITE.getIndex(),
        IndexedColors.DARK_BLUE.getIndex(),
        IndexedColors.DARK_BLUE.getIndex(),
        IndexedColors.WHITE.getIndex(),
        IndexedColors.BLACK.getIndex()
    };
    private static final int[] RED_THEME= {
        IndexedColors.BLACK.getIndex(),
        IndexedColors.WHITE.getIndex(),
        IndexedColors.DARK_RED.getIndex(),
        IndexedColors.DARK_RED.getIndex(),
        IndexedColors.WHITE.getIndex(),
        IndexedColors.BLACK.getIndex()
    };
    private static final int[] GREEN_THEME= {
        IndexedColors.BLACK.getIndex(),
        IndexedColors.WHITE.getIndex(),
        IndexedColors.DARK_GREEN.getIndex(),
        IndexedColors.DARK_GREEN.getIndex(),
        IndexedColors.WHITE.getIndex(),
        IndexedColors.BLACK.getIndex()
    };

    private final int color;

    ColorSpec(final int color) {
        this.color= color;
    }

    public int color(final Theme theme) {
        final int[] colors;
        switch (theme) {
            case RED:
                colors= RED_THEME;
                break;
            case GREEN:
                colors= GREEN_THEME;
                break;
            case BLUE:
            default:
                colors= BLUE_THEME;
                break;
        }
        return colors[color];
    }
}
