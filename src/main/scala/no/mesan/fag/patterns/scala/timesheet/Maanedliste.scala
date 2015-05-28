package no.mesan.fag.patterns.scala.timesheet

import no.mesan.fag.patterns.scala.timesheet.external.TimeDataService
import org.apache.poi.ss.usermodel.{Row, Cell, PrintSetup, Workbook}
import no.mesan.fag.patterns.scala.timesheet.data.{DoubleMatrix, TimesheetEntry}
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import no.mesan.fag.patterns.scala.timesheet.format._

/**
 * Hvem har fakturert på hvilke prosjekter i en gitt måned.
 * Aktiviteter >= 8000 ekskluderes.
 */
class Maanedliste(year: Int, month: Int, source: TimeDataService) extends Sheets {

  def createMaanedliste: Workbook = {
    val headingTitle= Maanedliste.SheetTitle

    // Hent timedata for perioden
    var fullList=  List[TimesheetEntry]()
    var got: Int = 0
    def getAbunch:Boolean = {
      val entries= source.forYear(year, got)
      if (entries.isEmpty) return false
      fullList ++= entries
      true
    }
    while (getAbunch) got = fullList.size

    // Filtrer bort interne timer og andre måneder
    var list = Vector[TimesheetEntry]()
    for (entry <- fullList) {
      if (entry.activity<Maanedliste.InternStart && entry.when.monthOfYear.get == month) list +:= entry
    }

    // Grupper data
    val matrix= new DoubleMatrix
    for (entry <- list) {
      val what = entry.activity
      val hours= minutesToHours(entry)
      val who = entry.userID
      matrix.add(what.toString, who, hours)
    }

    // Lag en arbeidsbok med 1 side
    val workbook= new XSSFWorkbook
    val sheet= workbook.createSheet(Maanedliste.SheetName)
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
    colnum= makeCell(heading1, colnum, H1, styles) { cell:Cell => cell.setCellValue(f"$year%04d/$month%02d")}

    // Tabelloverskrift
    rownum += 1 // Hopp over 1 rad
    val tableHead = createRow(sheet, rownum, 40)
    rownum+=1
    colnum = 0
    for (header <- Vector("Bruker -- Aktivitet", "Sum") ++ matrix.colKeys(sorted=true))
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
        colnum= makeCell(row, colnum, Data, styles) { cell:Cell => matrix.get(c, rKey) foreach cell.setCellValue }
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

}

object Maanedliste {
  val SheetName = "Maanedliste"
  val SheetTitle = "Månedsoppgjør"
  val InternStart = 8000
}