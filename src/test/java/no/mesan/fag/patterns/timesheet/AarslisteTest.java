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

/** Test av timeliste. */
public class AarslisteTest {

    private Workbook wb;

    @Before
    public void setUp() throws Exception {
        final Aarsliste timeliste = new Aarsliste(2014, new TimeDataServer(new SmallTimeSource()));
        wb = timeliste.createAarsoversikt();
    }

    @Test
    public void checkWorkbookStructure()  {
        assertNotNull(wb);
        assertEquals(1, wb.getNumberOfSheets());
        final Sheet sheet = wb.getSheet(Aarsliste.SHEET_TITLE);
        assertNotNull(sheet);
        final int lastRowNum = sheet.getLastRowNum();
        assertEquals(5, lastRowNum);
        assertEquals(13, sheet.getRow(lastRowNum).getLastCellNum());
    }

    @Test
    public void checkWorkbookContents()  {
        final Sheet sheet = wb.getSheet(Aarsliste.SHEET_TITLE);
        assertEquals(Aarsliste.SHEET_TITLE, extractCell(sheet, 0, 0).getStringCellValue());
        assertEquals(10.0D, extractCell(sheet, 3, 3).getNumericCellValue(), 0.001D); // Tilfeldig entry
        assertEquals(21.5D, extractCell(sheet, 1, 5).getNumericCellValue(), 0.001D); // Totalsum
        assertEquals(18, extractCell(sheet, 1, 5).getCellStyle().getFillForegroundColor());
    }

    private static Cell extractCell(final Sheet sheet, final int cellnum, final int rownum) {
        return sheet.getRow(rownum).getCell(cellnum);
    }
}
