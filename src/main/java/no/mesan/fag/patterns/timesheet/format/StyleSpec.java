package no.mesan.fag.patterns.timesheet.format;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

/** Holder spesifikasjoner for en stil. */
public final class StyleSpec implements Cloneable {

    private boolean isBold= false;
    private boolean isItalic= false;
    private int fontHeigthInPoints= 10;
    private ColorSpec fgColor= ColorSpec.STD_FG_COLOR;
    private ColorSpec bgColor= ColorSpec.STD_BG_COLOR;
    private List<BorderSpec> borders= new ArrayList<>();
    private AlignmentSpec.Horizontal horizontal= AlignmentSpec.Horizontal.GEN;
    private AlignmentSpec.Vertical vertical= AlignmentSpec.Vertical.BOTTOM;

    private StyleSpec() { /*EMPTY*/ }

    @Override
    public StyleSpec clone() throws CloneNotSupportedException {
        final StyleSpec clone = (StyleSpec) super.clone();
        clone.borders = new ArrayList<>(this.borders);
        return clone;
    }

    /**
     * Returnerer en builder som tar utgangspunkt i et default oppsett (10-punkt, svart skrift, ingen rammer),
     * du legger bare til tillegg.
     *
     * @return Byggern
     */
    public static StyleBuilder newStyle() {
        return new StyleBuilder(new StyleSpec());
    }

    /**
     * Returnerer en builder som tar utgangspunkt i en eksisterende stil.
     *
     * @return Byggern
     */
    public static StyleBuilder newStyleFrom(final StyleSpec spec) {
        try {
            return new StyleBuilder(spec.clone());
        }
        catch (final CloneNotSupportedException e) {
            throw new RuntimeException("Shouldn't happen", e);
        }
    }

    public static class StyleBuilder {
        private final StyleSpec spec;

        public StyleBuilder(final StyleSpec styleSpec) {
            spec = styleSpec;
        }

        /** Fet skrift. */
        public StyleBuilder bold() {
            spec.isBold= true;
            return this;
        }

        /** Ikke-fet skrift (etter kopi). */
        public StyleBuilder unbold() {
            spec.isBold= false;
            return this;
        }

        /** Kursiv. */
        public StyleBuilder italic() {
            spec.isItalic= true;
            return this;
        }

        /** Ikke-kursiv (etter kopi). */
        public StyleBuilder unitalic() {
            spec.isItalic= false;
            return this;
        }

        /** Fontstørrelse (i punkter). */
        public StyleBuilder size(final int heigthInPoints) {
            spec.fontHeigthInPoints= heigthInPoints;
            return this;
        }

        /** Tekstfarge. */
        public StyleBuilder fgColor(final ColorSpec color) {
            spec.fgColor= color;
            return this;
        }

        /** Fyllfarge. */
        public StyleBuilder bgColor(final ColorSpec color) {
            spec.bgColor= color;
            return this;
        }

        /** Standard fylt bakgrunn. */
        public StyleBuilder shaded() {
            fgColor(ColorSpec.SHADE_FG_COLOR);
            return bgColor(ColorSpec.SHADE_BG_COLOR);
        }

        /** Sett alle rammer til gitt tykkelse. */
        public StyleBuilder allBorders(final BorderSpec.BorderLine border) {
            for (final BorderSpec.BorderEdge edge : BorderSpec.BorderEdge.values()) {
                spec.borders.add(new BorderSpec(edge, border));
            }
            return this;
        }

        /** Sett alle rammer til tykkelse tynn. */
        public StyleBuilder allBorders() {
            return allBorders(BorderSpec.BorderLine.THIN_STD);
        }

        /** Sett venstre ramme til gitt tykkelse. */
        public StyleBuilder leftBorder(final BorderSpec.BorderLine border) {
            spec.borders.add(new BorderSpec(BorderSpec.BorderEdge.LEFT, border));
            return this;
        }

        /** Sett venstre ramme til tykkelse tynn. */
        public StyleBuilder leftBorder() {
            return leftBorder(BorderSpec.BorderLine.THIN_STD);
        }

        /** Sett høyre ramme til gitt tykkelse. */
        public StyleBuilder rightBorder(final BorderSpec.BorderLine border) {
            spec.borders.add(new BorderSpec(BorderSpec.BorderEdge.RIGHT, border));
            return this;
        }

        /** Sett høyre ramme til tykkelse tynn. */
        public StyleBuilder rightBorder() {
            return rightBorder(BorderSpec.BorderLine.THIN_STD);
        }

        /** Sett øvre ramme til gitt tykkelse. */
        public StyleBuilder topBorder(final BorderSpec.BorderLine border) {
            spec.borders.add(new BorderSpec(BorderSpec.BorderEdge.TOP, border));
            return this;
        }

        /** Sett øvre ramme til tykkelse tynn. */
        public StyleBuilder topBorder() {
            return topBorder(BorderSpec.BorderLine.THIN_STD);
        }

        /** Sett nedre ramme til gitt tykkelse. */
        public StyleBuilder bottomBorder(final BorderSpec.BorderLine border) {
            spec.borders.add(new BorderSpec(BorderSpec.BorderEdge.BOTTOM, border));
            return this;
        }

        /** Sett nedre ramme til tykkelse tynn. */
        public StyleBuilder bottomBorder() {
            return bottomBorder(BorderSpec.BorderLine.THIN_STD);
        }

        /** Sett venstrejustering av tekst. */
        public StyleBuilder leftAlign() {
            spec.horizontal= AlignmentSpec.Horizontal.LEFT;
            return this;
        }

        /** Sett høyrejustering av tekst. */
        public StyleBuilder rightAlign() {
            spec.horizontal= AlignmentSpec.Horizontal.RIGHT;
            return this;
        }

        /** Sett sentrering av tekst. */
        public StyleBuilder centerAlign() {
            spec.horizontal= AlignmentSpec.Horizontal.CENTER;
            return this;
        }

        /** Sett "generell" justering av tekst (dataavhengig). */
        public StyleBuilder genAlign() {
            spec.horizontal= AlignmentSpec.Horizontal.GEN;
            return this;
        }

        /** Sett plassering i toppen av cellen. */
        public StyleBuilder verticalTop() {
            spec.vertical= AlignmentSpec.Vertical.TOP;
            return this;
        }

        /** Sett plassering i bunnen av cellen. */
        public StyleBuilder verticalBottom() {
            spec.vertical= AlignmentSpec.Vertical.BOTTOM;
            return this;
        }

        /** Sett vertikal plassering sentrert i cellen. */
        public StyleBuilder verticalCenter() {
            spec.vertical= AlignmentSpec.Vertical.CENTER;
            return this;
        }

        public StyleSpec build() {
            return spec;
        }
    }

    /**
     * Oversett spesifkasjon til Excel-stil.
     *
     * @param wb Arbeidsbokreferanse
     * @return Stilen
     */
    public CellStyle createStyle(final Workbook wb) {
        final CellStyle style = wb.createCellStyle();
        makeFont(wb, style);
        if (bgColor!=null) {
            style.setFillForegroundColor((short) bgColor.color());
            style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        }
        style.setAlignment((short) horizontal.align());
        style.setVerticalAlignment((short) vertical.align());
        for (final BorderSpec border : borders) {
            final BorderSpec.BorderLine line = border.line();
            switch (border.edge()) {
                case TOP:
                    style.setBorderTop((short)line.border());
                    style.setTopBorderColor((short) line.color().color());
                    break;
                case BOTTOM:
                    style.setBorderBottom((short) line.border());
                    style.setBottomBorderColor((short) line.color().color());
                    break;
                case LEFT:
                    style.setBorderLeft((short) line.border());
                    style.setLeftBorderColor((short) line.color().color());
                    break;
                case RIGHT:
                    style.setBorderRight((short) line.border());
                    style.setRightBorderColor((short) line.color().color());
                    break;
            }
        }
        return style;
    }

    private void makeFont(final Workbook wb, final CellStyle style) {
        final Font font = wb.createFont();
        font.setFontHeightInPoints((short) fontHeigthInPoints);
        if (fgColor!=null) font.setColor((short) fgColor.color());
        if (isBold) font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        if (isItalic) font.setItalic(true);
        style.setFont(font);
    }

    @Override
    public String toString() {
        return new StringBuilder("StyleSpec{")
          .append("bold=").append(isBold)
          .append(", italic=").append(isItalic)
          .append(", fontHeigthInPoints=").append(fontHeigthInPoints)
          .append(", fgColor=").append(fgColor)
          .append(", bgColor=").append(bgColor)
          .append(", borders=").append(borders)
          .append(", horizontal=").append(horizontal)
          .append(", vertical=").append(vertical)
          .append('}')
          .toString();
    }
}
