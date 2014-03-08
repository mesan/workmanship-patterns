package no.mesan.fag.patterns.timesheet;

import no.mesan.fag.patterns.timesheet.external.SmallTimeSource;
import no.mesan.fag.patterns.timesheet.external.TimeDataServer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/** Test av Maanedliste. */
public class MaanedlisteTest {

    private Workbook wb;

    @Before
    public void setUp() throws Exception {
        final Maanedliste maanedliste = new Maanedliste(2014, 1, new TimeDataServer(new SmallTimeSource()));
        wb = maanedliste.createMaanedliste();
        maanedliste.writeToFile("xxx", wb);
    }

    @Test
    public void checkWorkbookStructure()  {
        assertNotNull(wb);
        assertEquals(1, wb.getNumberOfSheets());
        final Sheet sheet = wb.getSheet(Maanedliste.SHEET_NAME);
        assertNotNull(sheet);
        final int lastRowNum = sheet.getLastRowNum();
        assertEquals(5, lastRowNum);
        assertEquals(4, sheet.getRow(lastRowNum).getLastCellNum());
    }

    @Test
    public void checkWorkbookContents()  {
        final Sheet sheet = wb.getSheet(Maanedliste.SHEET_NAME);
        assertEquals(Maanedliste.SHEET_TITLE, extractCell(sheet, 0, 0).getStringCellValue());
        assertEquals(9.5D, extractCell(sheet, 2, 3).getNumericCellValue(), 0.001D); // Tilfeldig entry
        assertEquals(11.5D, extractCell(sheet, 1, 5).getNumericCellValue(), 0.001D); // Totalsum
        assertEquals(18, extractCell(sheet, 1, 5).getCellStyle().getFillForegroundColor());
    }

    private static Cell extractCell(final Sheet sheet, final int cellnum, final int rownum) {
        return sheet.getRow(rownum).getCell(cellnum);
    }
}
