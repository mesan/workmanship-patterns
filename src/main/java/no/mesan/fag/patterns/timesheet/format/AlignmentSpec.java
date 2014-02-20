package no.mesan.fag.patterns.timesheet.format;

import org.apache.poi.ss.usermodel.CellStyle;

/**
 * Mulige alignments.
 */
public interface AlignmentSpec {
    public enum Horizontal {
        GEN(CellStyle.ALIGN_GENERAL),
        LEFT(CellStyle.ALIGN_LEFT),
        RIGHT(CellStyle.ALIGN_RIGHT),
        CENTER(CellStyle.ALIGN_CENTER);

        private final int align;

        private Horizontal(final short align) {
            this.align = align;
        }

        public int align() {
            return this.align;
        }
    }

    public enum Vertical {
        BOTTOM(CellStyle.VERTICAL_BOTTOM),
        CENTER(CellStyle.VERTICAL_CENTER),
        TOP(CellStyle.VERTICAL_TOP);

        private final int align;

        private Vertical(final short align) {
            this.align = align;
        }

        public int align() {
            return this.align;
        }
    }
}
