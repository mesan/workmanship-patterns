package no.mesan.fag.patterns.scala.timesheet

import no.mesan.fag.patterns.scala.timesheet.external.TimeDataService
import no.mesan.fag.patterns.scala.timesheet.data.{TimesheetEntry, DoubleMatrix}
import no.mesan.fag.patterns.scala.timesheet.format._

import org.apache.poi.ss.usermodel._
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import org.joda.time.LocalDate

/** Timeliste for en enkelt bruker for en måned. */
class Timeliste(user: String, year: Int, month: Int, source: TimeDataService) extends Sheets {

  def createTimeliste: Workbook = {
    val headingTitle: String = Timeliste.SheetTitle

    // Hent timedata for bruker
    var fullList=  List[TimesheetEntry]()
    var got: Int = 0
    def getAbunch:Boolean = {
      val entries= source.forEmployee(user, got)
      if (entries.isEmpty) return false
      fullList ++= entries
      true
    }
    while (getAbunch) got = fullList.size

    // Filtrer for aktuelt tidsrom
    var list = Vector[TimesheetEntry]()
    for (entry <- fullList) {
      val when = entry.when
      if (when.year.get == year && when.monthOfYear.get == month) list +:= entry
    }

    // Grupper data
    val matrix= new DoubleMatrix
    for (i<- 1 until new LocalDate(year, month, 1).dayOfMonth.getMaximumValue) matrix.ensureCol(dayRef(i))
    for (entry <- list) {
      val what = entry.activity
      val hours= minutesToHours(entry)
      val day = entry.when.getDayOfMonth
      matrix.add(dayRef(day), what.toString, hours)
    }

    // Lag en arbeidsbok med 1 side
    val workbook= new XSSFWorkbook
    val sheet= workbook.createSheet(Timeliste.SheetTitle)
    val printSetup: PrintSetup = sheet.getPrintSetup
    printSetup.setLandscape(true)
    sheet.setFitToPage(true)
    sheet.setHorizontallyCenter(true)

    // Lag nødvendige stiler
    val styles = StyleFactory.styleSetup(workbook)
    var rownum= 0
    var colnum= 0
    val heading1 = createRow(sheet, rownum, 45)
    rownum+=1
    colnum= makeCell(heading1, colnum, H1, styles) { cell:Cell => cell.setCellValue(headingTitle)}
    colnum= makeCell(heading1, colnum, H1, styles) { cell:Cell => cell.setCellValue(user)}
    colnum= makeCell(heading1, colnum, H1, styles) { cell:Cell => cell.setCellValue(s"$year")}
    colnum= makeCell(heading1, colnum, H1, styles) { cell:Cell => cell.setCellValue(s"/ $month")}

    // Tabelloverskrift
    rownum += 1 // Hopp over 1 rad
    val tableHead = createRow(sheet, rownum, 40)
    rownum+=1
    colnum = 0
    for (header <- Vector("Aktivitet", "Sum") ++ matrix.colKeys(sorted=true))
      colnum= makeCell(tableHead, colnum, if (colnum < 2) TableHeadLeft else TableHead, styles) {
        cell=> cell.setCellValue(header)}

    // Datalinjer
    for (rKey <- matrix.rowKeys(sorted=true)) {
      // Index
      colnum = 0
      val row= createRow(sheet, rownum)
      rownum +=1
      colnum= makeCell(row, colnum, Col1, styles) { cell:Cell => cell.setCellValue(rKey)}
      // Sum
      val ref = cellRef(colnum+2, rownum) + ":" + cellRef(matrix.cSize + 2, rownum)
      colnum= makeCell(row, colnum, ColN, styles) { cell:Cell => cell.setCellFormula("SUM(" + ref + ")")}
      // Data
      for (c <- matrix.colKeys(sorted=true))
        colnum= makeCell(row, colnum, Data, styles) { cell:Cell => matrix.get(c, rKey) map cell.setCellValue }
    }
    // Sumlinje
    val row = createRow(sheet,rownum)
    colnum = 0
    colnum= makeCell(row, colnum, Sum1, styles) { cell:Cell => cell.setCellValue("SUM")}
    for (i <- 1 to matrix.cSize+1) {
      val ref = cellRef(i + 1, 4) + ":" + cellRef(i + 1, rownum)
      colnum= makeCell(row, colnum, Sums, styles) { cell:Cell => cell.setCellFormula("SUM(" + ref + ")")}
    }

    // Rekalkuler
    val evaluator= workbook.getCreationHelper.createFormulaEvaluator
    import scala.collection.JavaConversions._
    for (r: Row <- sheet.rowIterator();
         c: Cell <- r) {
      if (c.getCellType == Cell.CELL_TYPE_FORMULA)  evaluator.evaluateFormulaCell(c)
    }
    for (i <- 0 to matrix .cSize+1) {
      sheet.autoSizeColumn(i)
      sheet.setColumnWidth(i, (1.05 * sheet.getColumnWidth(i)).asInstanceOf[Int])
    }
    workbook
  }

  private def dayRef(i: Int): String = f"$i%02d.$month%02d"
}

object Timeliste {
  val SheetTitle = "Timeliste"
}
