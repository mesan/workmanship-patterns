package no.mesan.fag.patterns.timesheet.format;

import org.apache.poi.ss.usermodel.CellStyle;

/** Holder ulike kantlinjer. */
public class BorderSpec {
    private final BorderEdge edge;
    private final BorderLine line;

    public BorderEdge edge() {
        return edge;
    }

    public BorderLine line() {
        return line;
    }

    public BorderSpec(final BorderEdge edge, final BorderLine line) {
        this.edge = edge;
        this.line = line;
    }

    public enum BorderEdge { TOP, BOTTOM, LEFT, RIGHT }

    public enum BorderLine { // Utterly borderline!
        MEDIUM_STD(ColorSpec.DATA_GRID_COLOR, CellStyle.BORDER_MEDIUM),
        THIN_STD(ColorSpec.DATA_GRID_COLOR, CellStyle.BORDER_THIN),
        THICK_STD(ColorSpec.DATA_GRID_COLOR, CellStyle.BORDER_THICK);

        private final ColorSpec color;
        private final int border;

        BorderLine(final ColorSpec color, final int border) {
            this.color = color;
            this.border = border;
        }

        ColorSpec color() {
            return this.color;
        }

        int border() {
            return this.border;
        }
    }
}
