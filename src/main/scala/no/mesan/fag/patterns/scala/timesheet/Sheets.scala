package no.mesan.fag.patterns.scala.timesheet

import no.mesan.fag.patterns.scala.timesheet.external._
import no.mesan.fag.patterns.scala.timesheet.data.{DoubleMatrix, TimesheetEntry}
import no.mesan.fag.patterns.scala.timesheet.facade.SheetCell
import no.mesan.fag.patterns.scala.timesheet.format._

import java.io.FileOutputStream

import org.apache.poi.ss.usermodel._
import org.apache.poi.xssf.usermodel.XSSFWorkbook

/** Superklasse for timelister. */
abstract class Sheets {

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

  protected def dataRetrieve(service: TimeDataService, filterFun: TimesheetEntry=>Boolean): List[TimesheetEntry]=
    retrieve(service).toList filter filterFun
  def retrieve(service: TimeDataService): Iterable[TimesheetEntry]

  protected def dataGroup(entries: List[TimesheetEntry]): DoubleMatrix = {
    val matrix= new DoubleMatrix
    dataExtraHeadings(matrix)
    for (entry <- entries) {
      val (colRef, rowRef) = colRow(entry)
      matrix.add(colRef, rowRef, minutesToHours(entry))
    }
    matrix
  }
  protected def dataExtraHeadings(matrix: DoubleMatrix): Unit = Unit
  def colRow(entry: TimesheetEntry): (String, String)

  protected def createWorkbook(title: String): Sheet = {
    val sheet= (new XSSFWorkbook).createSheet(title)
    sheet.getPrintSetup.setLandscape(true)
    sheet.setFitToPage(true)
    sheet.setHorizontallyCenter(true)
    sheet
  }

  protected def createHeading(sheet: Sheet, styles: Map[StyleName, CellStyle]) {
    var colnum= 0
    val heading1 = createRow(sheet, 0, 45)
    for (s<-headingTexts()) colnum= makeCell(heading1, colnum, H1, styles) { cell:Cell => cell.setCellValue(s)}
  }
  def headingTexts(): List[String]

  protected def createTableHead(sheet: Sheet, matrix: DoubleMatrix, styles: Map[StyleName, CellStyle],
                                title: String, sortedCols: Boolean) {
    val tableHead = createRow(sheet, 2, 40)
    var colnum = 0
    for (header <- Vector(title, "Sum") ++ matrix.colKeys(sortedCols))
      colnum= makeCell(tableHead, colnum, if (colnum < 2) TableHeadLeft else TableHead, styles) {
        cell=> cell.setCellValue(header)
      }
  }

  protected def createDataGrid(sheet: Sheet, matrix: DoubleMatrix, styles: Map[StyleName, CellStyle], sortedCols: Boolean) {
    var rownum= 3
    for (rKey <- matrix.rowKeys(sorted=true)) {
      var colnum= 0
      val row= createRow(sheet, rownum)
      rownum +=1
      colnum= makeCell(row, colnum, Col1, styles) { cell:Cell => cell.setCellValue(rKey)}
      // Sum
      val ref = SheetCell.rangeRef(colnum+2, rownum, matrix.cSize + 2, rownum)
      colnum= makeCell(row, colnum, ColN, styles) { cell:Cell => cell.setCellFormula("SUM(" + ref + ")")}
      // Data
      for (c <- matrix.colKeys(sortedCols))
        colnum= makeCell(row, colnum, Data, styles) { cell:Cell => matrix.get(c, rKey) foreach cell.setCellValue }
    }
  }

  protected def createSums(sheet: Sheet, matrix: DoubleMatrix, styles: Map[StyleName, CellStyle]) {
    var colnum = 0
    val rownum = sheet.getLastRowNum+1
    val row = createRow(sheet,rownum)
    colnum= makeCell(row, colnum, Sum1, styles) { cell:Cell => cell.setCellValue("SUM")}
    for (i <- 1 to matrix.cSize+1) {
      val ref = SheetCell.rangeRef(i + 1, 4, i + 1, rownum)
      colnum= makeCell(row, colnum, Sums, styles) { cell:Cell => cell.setCellFormula("SUM(" + ref + ")")}
    }
  }

  protected def finish(sheet: Sheet, cols: Int) {
    import scala.collection.JavaConversions._
    // Rekalkuler
    val evaluator= sheet.getWorkbook.getCreationHelper.createFormulaEvaluator
    for (r: Row <- sheet.rowIterator();
         c: Cell <- r)
      if (c.getCellType == Cell.CELL_TYPE_FORMULA) evaluator.evaluateFormulaCell(c)
    for (i <- 0 to cols) {
      sheet.autoSizeColumn(i)
      sheet.setColumnWidth(i, (1.05 * sheet.getColumnWidth(i)).asInstanceOf[Int])
    }
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
  val mndListe= new Maanedliste(2014, 2, source)
  val wb2 = mndListe.createMaanedliste
  mndListe.writeToFile("Månedsoppgjør-scala", wb2)
  val aarsListe= new Aarsliste(2014, source)
  val wb3 = aarsListe.createAarsoversikt
  aarsListe.writeToFile("Årsoversikt-scala", wb3)
  val ukeListe = new Ukeliste(2014, 1, 15, source)
  val wb4 = ukeListe.createUkeliste
  ukeListe.writeToFile("Ukeoversikt-scala", wb4)
}
