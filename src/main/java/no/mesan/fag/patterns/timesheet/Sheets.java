package no.mesan.fag.patterns.timesheet;

import no.mesan.fag.patterns.timesheet.data.TimesheetEntry;
import no.mesan.fag.patterns.timesheet.external.TimeDataServer;
import no.mesan.fag.patterns.timesheet.external.TimeSource;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Superklasse for timelister.
 */
public class Sheets {

    public static void main(final String[] args) throws Exception {
        v1();
//        v2();
    }

    private static void v1() throws IOException {
        final TimeDataServer source = new TimeDataServer(new TimeSource());
        final Timeliste timeliste = new Timeliste("larsr", 2014, 2, source);
        final Workbook wb1 = timeliste.createTimeliste();
        timeliste.writeToFile("Timeliste", wb1);
        final Maanedliste mndListe = new Maanedliste(2014, 2, source);
        final Workbook wb2 = mndListe.createMaanedliste();
        mndListe.writeToFile("Månedsoppgjør", wb2);
        final Aarsliste aarsListe = new Aarsliste(2014, source);
        final Workbook wb3 = aarsListe.createAarsoversikt();
        aarsListe.writeToFile("Årsoversikt", wb3);
    }
/*
    private static void v2() throws IOException {
        final TimeDataServer source = new TimeDataServer(new SmallTimeSource());
        final Timeliste timeliste = new Timeliste("A", 2014, 1, source);
        final Workbook wb1 = timeliste.createTimeliste();
        timeliste.writeToFile("Timeliste2", wb1);
        final Maanedliste mndListe = new Maanedliste(2014, 1, source);
        final Workbook wb2 = mndListe.createMaanedliste();
        mndListe.writeToFile("Månedsoppgjør2", wb2);
        final Aarsliste aarsListe = new Aarsliste(2014, source);
        final Workbook wb3 = aarsListe.createAarsoversikt();
        aarsListe.writeToFile("Årsoversikt2", wb3);
    }
*/

    /**
     * Lag en cellereferanse (type A2B5) for en gitt kolonne+rad.
     * @param col Kolonne
     * @param row Rad
     * @return Celleref
     */
    String cellRef(final int col, final int row) {
        final int col0= col-1;
        final int ii= col0/26;
        final int i= col0%26;
        final StringBuilder b= new StringBuilder();
        if (ii>0) b.append((char) ('A'+ii-1));
        return b.append((char) ('A'+i))
                .append(row)
                .toString();
    }

    void writeToFile(final String bookName, final Workbook workbook) throws IOException {
        final String fileName = bookName + ".xlsx";
        final FileOutputStream out = new FileOutputStream(fileName);
        workbook.write(out);
        out.close();
    }

    Row createRow(final Sheet sheet, final int rownum, final int height) {
        final Row row;
        row= sheet.createRow(rownum);
        if (height>0) row.setHeightInPoints(height);
        return row;
    }

    double minutesToHours(final TimesheetEntry entry) {return (entry.getMinutes() / 30) / 2.0;}
}
