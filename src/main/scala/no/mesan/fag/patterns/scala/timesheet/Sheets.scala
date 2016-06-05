package no.mesan.fag.patterns.scala.timesheet

import no.mesan.fag.patterns.scala.timesheet.external.{TimeDataServer, TimeDataService, TimeSource}
import no.mesan.fag.patterns.scala.timesheet.data.{DoubleMatrix, TimesheetEntry}
import no.mesan.fag.patterns.scala.timesheet.format.{StyleFactory, StyleName}
import java.io.FileOutputStream

import org.apache.poi.ss.usermodel._

/** Superklasse for timelister. */
abstract class Sheets {

  /// HINT Foreslår følgende struktur på template-metoden:
  /**
   * Dette er hovedrutinen for å lage rapporter.
   * Du må selvfølgelig overstyre alle abstrakte rutiner (! under), men du kan også gripe inn på andre nivåer --
   * her er strukturen:
   * <pre>
   * dataRetrieve +  filter (funksjonsarg)
   *   retrieve!
   * dataGroup
   *    dataExtraHeadings -- evt. tilleggsoverskrifter, default ingen
   *    colRow !
   * createWorkbook
   * createHeading
   *    headingTexts!
   * createTableHead
   * createDataGrid
   * createSums
   * finish
   * </pre>
   *
   * @param dataService Tjeneste for å hente data
   * @param title Navn på arbeidsboken
   * @param headTitle Øverste venstre overskrift i tabellen
   * @param sortedCols Om kolonneoverskriftene skal være sortert
   * @return Resultat
   */
  protected final def generateReport(dataService: TimeDataService, title: String, headTitle: String,
                                     sortedCols: Boolean = true) (filter: TimesheetEntry=>Boolean) : Workbook = {
    val list = dataRetrieve(dataService, filter)
    val matrix = dataGroup(list)
    val sheet = createWorkbook(title)
    val workbook = sheet.getWorkbook
    val styles = StyleFactory.styleSetup(workbook)
    createHeading(sheet, styles)
    createTableHead(sheet, matrix, styles, headTitle, sortedCols)
    createDataGrid(sheet, matrix, styles, sortedCols)
    createSums(sheet, matrix, styles)
    finish(sheet, matrix.cSize)
    workbook
  }

  /// HINT Metodeforslag, nivå 1
  protected def dataRetrieve(service: TimeDataService, filterFun: TimesheetEntry => Boolean): List[TimesheetEntry] = ???
  protected def dataGroup(entries: List[TimesheetEntry]): DoubleMatrix = ???
  protected def createWorkbook(title: String): Sheet = ???
  protected def createHeading(sheet: Sheet, styles: Map[StyleName, CellStyle]) = ???
  protected def createTableHead(sheet: Sheet, matrix: DoubleMatrix, styles: Map[StyleName, CellStyle], title: String, sortedCols: Boolean) {}
  protected def createDataGrid(sheet: Sheet, matrix: DoubleMatrix, styles: Map[StyleName, CellStyle], sortedCols: Boolean) {}
  protected def createSums(sheet: Sheet, matrix: DoubleMatrix, styles: Map[StyleName, CellStyle]) {}
  protected def finish(sheet: Sheet, cols: Int) {}


  /**
   * Lag en cellereferanse (type A2B5) for en gitt kolonne+rad.
    *
    * @param col Kolonne
   * @param row Rad
   * @return Celleref
   */
  private[timesheet] def cellRef(col: Int, row: Int): String = {
    val col0 = col - 1
    val ii = col0 / 26
    val i = col0 % 26
    val pfx: String = if (ii > 0) s"${('A' + ii - 1).asInstanceOf[Char]}" else ""
    s"${pfx}${('A'+i).asInstanceOf[Char]}$row"
  }

  private[timesheet] def makeCell(row: Row, colnum:Int, style: StyleName, styles: Map[StyleName, CellStyle])
                                 (f: Cell=>Unit): Int = {
    val cell = row.createCell(colnum)
    f(cell)
    styles.get(style) foreach cell.setCellStyle
    colnum+1
  }

  private[timesheet] def writeToFile(bookName: String, workbook: Workbook) {
    val fileName = bookName + ".xlsx"
    val out = new FileOutputStream(fileName)
    workbook.write(out)
    out.close
  }

  private[timesheet] def createRow(sheet: Sheet, rownum: Int, height: Int = 0): Row = {
    val row= sheet.createRow(rownum)
    if (height > 0) row.setHeightInPoints(height)
    row
  }

  private[timesheet] def minutesToHours(entry: TimesheetEntry): Double =  (entry.minutes / 30) / 2.0
}

object Sheets extends App {
  // ColorSpec.theme= RedTheme
  val source = new TimeDataServer(TimeSource)
  val timeliste = new Timeliste("larsr", 2014, 2, source)
  val wb1 = timeliste.createTimeliste
  timeliste.writeToFile("Timeliste-scala", wb1)
  val mndListe: Maanedliste = new Maanedliste(2014, 2, source)
  val wb2: Workbook = mndListe.createMaanedliste
  mndListe.writeToFile("Månedsoppgjør-scala", wb2)
  val aarsListe: Aarsliste = new Aarsliste(2014, source)
  val wb3: Workbook = aarsListe.createAarsoversikt
  aarsListe.writeToFile("Årsoversikt-scala", wb3)
}
