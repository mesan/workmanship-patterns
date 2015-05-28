package no.mesan.fag.patterns.timesheet.format;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

/** Oppretter stiler for et worksheet. */
public final class StyleFactory {

    /** Tilgjengelige stiler. */
    public enum StyleName {
        H1, TBL_HEAD, TBL_HEAD_LEFT, COL1, COLN, SUMS, SUM1, DATA
    }

    /** Lag et stilbibliotek. */
    public static Map<StyleName, CellStyle> styleSetup(final Workbook wb) {
        return createWbStyles(wb, styleSetup());
    }

    private static Map<StyleName, StyleSpec> styleSetup(){
        final Map<StyleName, StyleSpec> styles = new HashMap<>();
        final BorderSpec.BorderLine mediumBorder = BorderSpec.BorderLine.MEDIUM_STD;
        final StyleSpec h1 = StyleSpec.newStyle()
                                      .bold().size(15).fgColor(ColorSpec.STD_H_COLOR).centerAlign().verticalCenter()
                                      .build();
        final StyleSpec tblHead = StyleSpec.newStyleFrom(h1).size(12).allBorders().rightAlign().build();
        final StyleSpec tblHeadLeft = StyleSpec.newStyleFrom(tblHead).italic().rightBorder(mediumBorder).build();
        final StyleSpec data = StyleSpec.newStyle().allBorders().build();
        final StyleSpec col1= StyleSpec.newStyleFrom(data).bold().leftAlign().rightBorder(mediumBorder).build();
        final StyleSpec colN= StyleSpec.newStyleFrom(data).italic().rightAlign().rightBorder(mediumBorder).build();
        final StyleSpec sums = StyleSpec.newStyle().italic().size(12).shaded().allBorders(mediumBorder).build();
        final StyleSpec sum1 = StyleSpec.newStyleFrom(sums).unitalic().bold().build();
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
