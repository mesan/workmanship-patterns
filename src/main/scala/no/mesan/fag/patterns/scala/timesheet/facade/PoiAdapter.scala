package no.mesan.fag.patterns.scala.timesheet.facade

import no.mesan.fag.patterns.scala.timesheet.format.{StyleName, Styles}
import no.mesan.fag.patterns.scala.timesheet.data.ValueMatrix
import org.apache.poi.ss.usermodel._
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream

// HINT import no.mesan.fag.patterns.scala.timesheet.facade.{DoubleCell, EmptyCell, FormulaCell, StringCell}

import scala.collection.JavaConversions._

/** Adapter vår fasade til POI Workbook. */
class PoiAdapter(title: String, map: Map[StyleName, Styles]) {
  private val workbook = new XSSFWorkbook
  private val sheet = workbook.createSheet(title)
  private val styles: Map[StyleName, CellStyle] =
    for (entry <- map) yield entry._1 -> entry._2.createStyle(workbook)

  printSetup

  /** Sett utskriftsoppsett. */
  private def printSetup {
    val printSetup: PrintSetup = sheet.getPrintSetup
    printSetup.setLandscape(true)
    sheet.setFitToPage(true)
    sheet.setHorizontallyCenter(true)
  }

  def create: Workbook = {
    val noOfCols = reevaluateSheet
    autoSizeSheet(noOfCols)
    workbook
  }

  /** Evaluer alle formler på et gitt ark på nytt. */
  private def reevaluateSheet: Int = {
    var maxCols = 0
    val evaluator: FormulaEvaluator = sheet.getWorkbook.getCreationHelper.createFormulaEvaluator
    for (r <- sheet.rowIterator()) {
      var cols = 0
      for (c <- r.cellIterator()) {
        cols += 1
        if (c.getCellType == Cell.CELL_TYPE_FORMULA)  evaluator.evaluateFormulaCell(c)
      }
      maxCols = Math.max(maxCols, cols)
    }
    maxCols
  }

  /** Autojuster alle kolonner på et ark. */
  private def autoSizeSheet(noOfColumns: Int) {
    for (i<-0 until noOfColumns) {
      sheet.autoSizeColumn(i)
      sheet.setColumnWidth(i, (1.05 * sheet.getColumnWidth(i)).asInstanceOf[Int])
    }
  }

  /** Legg til dataene. */
  def addData(values: SpreadSheet) =
    for (rowNum <- values.data.rowKeys(sorted=true)) createRow(values.data, rowNum, values.rowHeight(rowNum))

  private def createRow(data: ValueMatrix[Int, Int, SheetCell], rowNum: Int, height: Option[Int]) {
    val row = sheet.createRow(rowNum)
    height foreach(row.setHeightInPoints(_))
    for (colNum <- data.colKeys(sorted=true)) createCell(data.get(colNum, rowNum), colNum, row)
    /// HINT I stedet for forrige kan vi gjøre det slik
    /// val visitor = new PoiVisitor(row, styles)
    /// for (colNum <- data.colKeys(sorted=true);
    ///      cell<-data.get(colNum, rowNum))
    ///   visitor.visit(cell)
    }

  private def createCell(sheetCell: Option[SheetCell], colNum: Int, row: Row)  {
    for { sCell <- sheetCell
          cell = sCell.fillCell(row.createCell(colNum))
          name <- sCell.style
          style <- styles.get(name) } cell.setCellStyle(style)
  }
}

class PoiVisitor(row: Row, styles: Map[StyleName, CellStyle]) {
  var colnum= 0

  private def makeCell(cell: SheetCell): Cell = {
    val poiCell = row.createCell(colnum)
    colnum+=1
    for (styleName<- cell.style;
         style<-styles.get(styleName)) poiCell.setCellStyle(style)
    poiCell
  }

  def visit(cell: SheetCell) {
    val poiCell= makeCell(cell)
    cell match {
        /// HINT Pattern matching bør gjøre jobben. Da må du gjøre om disse klassene til case klasser
      /// case DoubleCell(d:Double, _) => ???
      /// case StringCell(s:String, _) => ???
      /// case FormulaCell(f: String, _) => ???
      /// case EmptyCell(_) =>
      case _ => throw new IllegalArgumentException(cell.toString)
    }
  }
}

object PoiAdapter {
  /**
   * Skriv en arbeidsbok til Excelfil.
   * @param bookName Boknavnet (suffikses med .xlsx)
   * @param workbook Referanse til boken
   * @throws java.io.IOException Verden er ikke perfekt
   */
  def writeToFile(bookName: String, workbook: Workbook) {
    val fileName: String = bookName + ".xlsx"
    val out: FileOutputStream = new FileOutputStream(fileName)
    workbook.write(out)
    out.close
  }
}
