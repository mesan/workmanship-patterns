package no.mesan.fag.patterns.timesheet.format;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.List;

/**
 * Holder spesifikasjoner for en stil.
 */
public class StyleSpec {

    private boolean isBold= false;
    private boolean isItalic= false;
    private int fontHeigthInPoints= 10;
    private ColorSpec fgColor= ColorSpec.STD_FG_COLOR;
    private ColorSpec bgColor= ColorSpec.STD_BG_COLOR;
    private List<BorderSpec> borders= new ArrayList<>();
    private AlignmentSpec.Horizontal horizontal= AlignmentSpec.Horizontal.GEN;
    private AlignmentSpec.Vertical vertical= AlignmentSpec.Vertical.BOTTOM;

    /**
     * Opprett ny spesifikasjon (ikke avhengig av Excel).
     *
     * @param bold Bold?
     * @param italic Italic?
     * @param points Punktstørrelse
     * @param fgColor Tekstfarge
     * @param bgColor Cellefarge (100% fill)
     * @param borderTop Linjetykkelse (eller null for ingen borderTop)
     * @param borderBottom Linjetykkelse (eller null for ingen borderBottom)
     * @param borderLeft Linjetykkelse (eller null for ingen borderLeft)
     * @param borderRight Linjetykkelse (eller null for ingen borderRight)
     * @param horizontal Horisontal alignment
     * @param vertical Vertikal alignment
     */
    public StyleSpec(final boolean bold, final boolean italic, final int points,
                     final ColorSpec fgColor, final ColorSpec bgColor, final BorderSpec.BorderLine borderTop,
                     final BorderSpec.BorderLine borderBottom, final BorderSpec.BorderLine borderLeft,
                     final BorderSpec.BorderLine borderRight, final AlignmentSpec.Horizontal horizontal,
                     final AlignmentSpec.Vertical vertical) {
        this.isBold = bold;
        this.isItalic = italic;
        this.fontHeigthInPoints = points;
        this.fgColor = fgColor;
        this.bgColor = bgColor;
        if (borderTop!=null) borders.add(new BorderSpec(BorderSpec.BorderEdge.TOP, borderTop));
        if (borderBottom!=null) borders.add(new BorderSpec(BorderSpec.BorderEdge.BOTTOM, borderBottom));
        if (borderLeft!=null) borders.add(new BorderSpec(BorderSpec.BorderEdge.LEFT, borderLeft));
        if (borderRight!=null) borders.add(new BorderSpec(BorderSpec.BorderEdge.RIGHT, borderRight));
        this.horizontal = horizontal;
        this.vertical = vertical;
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
        for (BorderSpec border : borders) {
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
