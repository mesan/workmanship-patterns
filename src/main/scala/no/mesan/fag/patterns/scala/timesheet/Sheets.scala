package no.mesan.fag.patterns.scala.timesheet

import no.mesan.fag.patterns.scala.timesheet.external.decorators.{TimeDataServiceLoggingDecorator,
                                                                  TimeDataServiceCachingDecorator}
import no.mesan.fag.patterns.scala.timesheet.command.{AsyncTaskExecutor, AsyncTask}
import no.mesan.fag.patterns.scala.timesheet.strategy._
import no.mesan.fag.patterns.scala.timesheet.external._
import no.mesan.fag.patterns.scala.timesheet.data.{DoubleMatrix, TimesheetEntry}
import no.mesan.fag.patterns.scala.timesheet.facade._
import no.mesan.fag.patterns.scala.timesheet.format._

import org.apache.poi.ss.usermodel._
import no.mesan.fag.patterns.scala.timesheet.facade.DoubleCell
import scala.Some
import no.mesan.fag.patterns.scala.timesheet.format.Styles
import no.mesan.fag.patterns.scala.timesheet.facade.StringCell
import no.mesan.fag.patterns.scala.timesheet.facade.EmptyCell

/** Superklasse for timelister. */
abstract class Sheets extends TimeRepresentationStrategy with TimeRepresentationHalfHours {

  /** Fargesetting. */
  private var theme: Option[Theme]= None  // Kunne også vært løst med traits

  /** Konverter til arbeidsbok. */
  def createBook: Workbook

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
    val sheet = new SpreadSheet(title, theme.getOrElse(BlueTheme))
    val styles = StyleFactory.styleSetup
    createHeading(sheet)
    createTableHead(sheet, matrix, headTitle, sortedCols)
    createDataGrid(sheet, matrix, sortedCols)
    createSums(sheet, matrix)
    finish(title, sheet, styles)
  }

  protected def dataRetrieve(service: TimeDataService, filterFun: TimesheetEntry=>Boolean): List[TimesheetEntry]=
    retrieve(service).toList filter filterFun
  def retrieve(service: TimeDataService): Iterable[TimesheetEntry]

  protected def dataGroup(entries: List[TimesheetEntry]): DoubleMatrix = {
    val matrix= new DoubleMatrix
    dataExtraHeadings(matrix)
    for (entry <- entries) {
      val (colRef, rowRef) = colRow(entry)
      matrix.add(colRef, rowRef, convertTime(entry.minutes))
    }
    matrix
  }
  protected def dataExtraHeadings(matrix: DoubleMatrix): Unit = Unit
  def colRow(entry: TimesheetEntry): (String, String)

  protected def createHeading(sheet: SpreadSheet) {
    var colnum= -1
    val rownum= 0
    sheet.setRowHeight(rownum, 45)
    for (s<-headingTexts) { colnum+=1; sheet.setCell(colnum, rownum, new StringCell(s, H1)) }
  }
  def headingTexts: List[String]

  protected def createTableHead(sheet: SpreadSheet, matrix: DoubleMatrix,  title: String, sortedCols: Boolean) {
    val rownum= 2
    sheet.setRowHeight(rownum, 40)
    var colnum = -1
    for (header <- Vector(title, "Sum") ++ matrix.colKeys(sortedCols)){
      colnum+=1
      sheet.setCell(colnum, rownum, new StringCell(header, if (colnum < 2) TableHeadLeft else TableHead))
    }
  }

  protected def createDataGrid(sheet: SpreadSheet, matrix: DoubleMatrix,  sortedCols: Boolean) {
    sheet.data.ensureRow(2) // Hopp over rad
    var rownum= 2
    var colnum = 0
    def createDataCols(rKey: String) {
      for (c <- matrix.colKeys(sortedCols)) {
        colnum += 1
        matrix.get(c, rKey) match {
          case Some(value) => sheet.setCell(colnum, rownum, new DoubleCell(value, Data))
          case None => sheet.setCell(colnum, rownum, new EmptyCell(Data))
        }
      }
    }
    def createSumCols(rKey: String) = {
      sheet.setCell(colnum, rownum, new StringCell(rKey, Col1))
      colnum += 1
      sheet.setCell(colnum, rownum, SheetCell.formulaSUM(colnum + 2, rownum + 1, 2 + matrix.cSize, rownum + 1, ColN))
    }
    for (rKey <- matrix.rowKeys(sorted=true)) {
      rownum +=1
      colnum= 0
      createSumCols(rKey)
      createDataCols(rKey)
    }
  }

  protected def createSums(sheet: SpreadSheet, matrix: DoubleMatrix) {
    val rownum = sheet.lastRowNum+1
    sheet.setCell(0, rownum, new StringCell("SUM", Sum1))
    for (i <- 1 to matrix.cSize+1)
     sheet.setCell(i, rownum, SheetCell.formulaSUM(i + 1, 4, i + 1, rownum, Sums))
  }

  protected def finish(title:String, sheet: SpreadSheet, styles: Map[StyleName, Styles]): Workbook = {
    val adapter = new PoiAdapter(title, theme.getOrElse(BlueTheme), styles)
    adapter.addData(sheet)
    adapter.create
  }

  private[timesheet] def writeToFile(bookName: String, workbook: Workbook) = PoiAdapter.writeToFile(bookName, workbook)

  /** Ny fargesetting. */
  def setTheme(theme: Theme) { this.theme= Some(theme) }
}

private class TimelisteTask(val name: String, val sheet: Sheets) extends AsyncTask {
  def whoAmI: String =  name
  def executeTask() = sheet.writeToFile(name, sheet.createBook)
}

object Sheets extends App {
  val source = new TimeDataServer(TimeSource)
  val timeliste = new Timeliste("larsr", 2014, 2, source)
  val mndListe= new Maanedliste(2014, 2, source)
  val aarsListe= new Aarsliste(2014, source)

  val ukeSource = new TimeDataServer(TimeSource) with TimeDataServiceCachingDecorator with TimeDataServiceLoggingDecorator
  val ukeListe = new Ukeliste(2014, 1, 15, ukeSource) with TimeRepresentationDays
  ukeListe.setTheme(RedTheme)

  val taskExecutor = new AsyncTaskExecutor
  taskExecutor.executeTasks(new TimelisteTask("Timeliste-scala", timeliste),
                            new TimelisteTask("Månedsoppgjør-scala", mndListe),
                            new TimelisteTask("Årsoversikt-scala", aarsListe),
                            new TimelisteTask("Ukeoversikt-scala", ukeListe))
}
