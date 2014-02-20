package no.mesan.fag.patterns.timesheet.format;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.Map;

/**
 * Oppretter stiler for et worksheet.
 */
public class StyleFactory {
    /** Tilgjengelige stiler. */
    public static enum StyleName {
        H1, TBL_HEAD, TBL_HEAD_LEFT, COL1, COLN, SUMS, SUM1, DATA
    }

    /** CLag et stilbibliotek. */
    public static Map<StyleName, CellStyle> styleSetup(final Workbook wb){
        final Map<StyleName, CellStyle> styles = new HashMap<>();
        styles.put(StyleName.H1, new StyleSpec(true, false, 15, ColorSpec.STD_H_COLOR, null, null, null, null, null,
                                               AlignmentSpec.Horizontal.CENTER, AlignmentSpec.Vertical.CENTER)
                                         .createStyle(wb));
        styles.put(StyleName.TBL_HEAD, new StyleSpec(true, false, 12, ColorSpec.STD_H_COLOR, ColorSpec.STD_BG_COLOR,
                                                     BorderSpec.BorderLine.THIN_STD, BorderSpec.BorderLine.THIN_STD,
                                                     BorderSpec.BorderLine.THIN_STD, BorderSpec.BorderLine.THIN_STD,
                                                     AlignmentSpec.Horizontal.RIGHT, AlignmentSpec.Vertical.CENTER)
                                               .createStyle(wb));
        styles.put(StyleName.TBL_HEAD_LEFT, new StyleSpec(true, true, 12, ColorSpec.STD_H_COLOR, ColorSpec.STD_BG_COLOR,
                                                          BorderSpec.BorderLine.THIN_STD,
                                                          BorderSpec.BorderLine.THIN_STD,
                                                          BorderSpec.BorderLine.THIN_STD,
                                                          BorderSpec.BorderLine.MEDIUM_STD,
                                                          AlignmentSpec.Horizontal.LEFT, AlignmentSpec.Vertical.CENTER)
                                                    .createStyle(wb));
        styles.put(StyleName.COL1, new StyleSpec(true, false, 10, null, null, BorderSpec.BorderLine.THIN_STD,
                                                 BorderSpec.BorderLine.THIN_STD, BorderSpec.BorderLine.THIN_STD,
                                                 BorderSpec.BorderLine.MEDIUM_STD, AlignmentSpec.Horizontal.LEFT,
                                                 AlignmentSpec.Vertical.BOTTOM)
                                           .createStyle(wb));
        styles.put(StyleName.DATA, new StyleSpec(false, false, 10, null, null, BorderSpec.BorderLine.THIN_STD,
                                                 BorderSpec.BorderLine.THIN_STD, BorderSpec.BorderLine.THIN_STD,
                                                 BorderSpec.BorderLine.THIN_STD, AlignmentSpec.Horizontal.GEN,
                                                 AlignmentSpec.Vertical.BOTTOM)
                                           .createStyle(wb));
        styles.put(StyleName.COLN, new StyleSpec(false, true, 10, null, null, BorderSpec.BorderLine.THIN_STD,
                                                 BorderSpec.BorderLine.THIN_STD, BorderSpec.BorderLine.THIN_STD,
                                                 BorderSpec.BorderLine.MEDIUM_STD, AlignmentSpec.Horizontal.RIGHT,
                                                 AlignmentSpec.Vertical.BOTTOM)
                                           .createStyle(wb));
        styles.put(StyleName.SUMS, new StyleSpec(false, true, 12, ColorSpec.SHADE_FG_COLOR, ColorSpec.SHADE_BG_COLOR,
                                                 BorderSpec.BorderLine.MEDIUM_STD, BorderSpec.BorderLine.MEDIUM_STD,
                                                 BorderSpec.BorderLine.MEDIUM_STD, BorderSpec.BorderLine.MEDIUM_STD,
                                                 AlignmentSpec.Horizontal.GEN, AlignmentSpec.Vertical.BOTTOM)
                                           .createStyle(wb));
        styles.put(StyleName.SUM1, new StyleSpec(true, false, 12, ColorSpec.SHADE_FG_COLOR, ColorSpec.SHADE_BG_COLOR,
                                                 BorderSpec.BorderLine.MEDIUM_STD, BorderSpec.BorderLine.MEDIUM_STD,
                                                 BorderSpec.BorderLine.MEDIUM_STD, BorderSpec.BorderLine.MEDIUM_STD,
                                                 AlignmentSpec.Horizontal.GEN, AlignmentSpec.Vertical.BOTTOM)
                                           .createStyle(wb));
        return styles;
    }

}
