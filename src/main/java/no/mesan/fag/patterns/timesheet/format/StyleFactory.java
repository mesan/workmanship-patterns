package no.mesan.fag.patterns.timesheet.format;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.Map;

/** Oppretter stiler for et worksheet. */
public final class StyleFactory {

    /** Tilgjengelige stiler. */
    public static enum StyleName {
        H1, TBL_HEAD, TBL_HEAD_LEFT, COL1, COLN, SUMS, SUM1, DATA
    }

    /** Lag et stilbibliotek. */
    public static Map<StyleName, CellStyle> styleSetup(final Workbook wb) {
        return createWbStyles(wb, styleSetup());
    }

    private static Map<StyleName, StyleSpec> styleSetup(){
        final Map<StyleName, StyleSpec> styles = new HashMap<>();
        final StyleSpec h1 = new StyleSpec(true, false, 15, ColorSpec.STD_H_COLOR, null, null, null, null, null,
                                           AlignmentSpec.Horizontal.CENTER, AlignmentSpec.Vertical.CENTER);
        final StyleSpec tblHead = new StyleSpec(true, false, 12, ColorSpec.STD_H_COLOR, ColorSpec.STD_BG_COLOR,
                                                BorderSpec.BorderLine.THIN_STD, BorderSpec.BorderLine.THIN_STD,
                                                BorderSpec.BorderLine.THIN_STD, BorderSpec.BorderLine.THIN_STD,
                                                AlignmentSpec.Horizontal.RIGHT, AlignmentSpec.Vertical.CENTER);
        final StyleSpec tblHeadLeft = new StyleSpec(true, true, 12, ColorSpec.STD_H_COLOR, ColorSpec.STD_BG_COLOR,
                                                    BorderSpec.BorderLine.THIN_STD, BorderSpec.BorderLine.THIN_STD,
                                                    BorderSpec.BorderLine.THIN_STD, BorderSpec.BorderLine.MEDIUM_STD,
                                                    AlignmentSpec.Horizontal.LEFT, AlignmentSpec.Vertical.CENTER);
        final StyleSpec col1 = new StyleSpec(true, false, 10, null, null, BorderSpec.BorderLine.THIN_STD,
                                             BorderSpec.BorderLine.THIN_STD, BorderSpec.BorderLine.THIN_STD,
                                             BorderSpec.BorderLine.MEDIUM_STD, AlignmentSpec.Horizontal.LEFT,
                                             AlignmentSpec.Vertical.BOTTOM);
        final StyleSpec data = new StyleSpec(false, false, 10, null, null, BorderSpec.BorderLine.THIN_STD,
                                              BorderSpec.BorderLine.THIN_STD, BorderSpec.BorderLine.THIN_STD,
                                              BorderSpec.BorderLine.THIN_STD, AlignmentSpec.Horizontal.GEN,
                                              AlignmentSpec.Vertical.BOTTOM);
        final StyleSpec colN = new StyleSpec(false, true, 10, null, null, BorderSpec.BorderLine.THIN_STD,
                                              BorderSpec.BorderLine.THIN_STD, BorderSpec.BorderLine.THIN_STD,
                                              BorderSpec.BorderLine.MEDIUM_STD, AlignmentSpec.Horizontal.RIGHT,
                                              AlignmentSpec.Vertical.BOTTOM);
        final StyleSpec sums = new StyleSpec(false, true, 12, ColorSpec.SHADE_FG_COLOR, ColorSpec.SHADE_BG_COLOR,
                                             BorderSpec.BorderLine.MEDIUM_STD, BorderSpec.BorderLine.MEDIUM_STD,
                                             BorderSpec.BorderLine.MEDIUM_STD, BorderSpec.BorderLine.MEDIUM_STD,
                                             AlignmentSpec.Horizontal.GEN, AlignmentSpec.Vertical.BOTTOM);
        final StyleSpec sum1 = new StyleSpec(true, false, 12, ColorSpec.SHADE_FG_COLOR, ColorSpec.SHADE_BG_COLOR,
                                              BorderSpec.BorderLine.MEDIUM_STD, BorderSpec.BorderLine.MEDIUM_STD,
                                              BorderSpec.BorderLine.MEDIUM_STD, BorderSpec.BorderLine.MEDIUM_STD,
                                              AlignmentSpec.Horizontal.GEN, AlignmentSpec.Vertical.BOTTOM);
        styles.put(StyleName.H1, h1);
        styles.put(StyleName.TBL_HEAD, tblHead);
        styles.put(StyleName.TBL_HEAD_LEFT, tblHeadLeft);
        styles.put(StyleName.COL1, col1);
        styles.put(StyleName.DATA, data);
        styles.put(StyleName.COLN, colN);
        styles.put(StyleName.SUMS, sums);
        styles.put(StyleName.SUM1, sum1);
        return styles;
    }

    private static Map<StyleName, CellStyle> createWbStyles(final Workbook wb, final Map<StyleName, StyleSpec> map){
        final Map<StyleName, CellStyle> styles = new HashMap<>();
        for (final Map.Entry<StyleName, StyleSpec> entry : map.entrySet()) {
            styles.put(entry.getKey(), entry.getValue().createStyle(wb));
        }
        return styles;
    }

}
