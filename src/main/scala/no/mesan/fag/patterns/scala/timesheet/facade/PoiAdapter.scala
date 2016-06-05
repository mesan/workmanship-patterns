package no.mesan.fag.patterns.scala.timesheet.facade

import no.mesan.fag.patterns.scala.timesheet.format.{Styles, StyleName}
import no.mesan.fag.patterns.scala.timesheet.data.ValueMatrix

import org.apache.poi.ss.usermodel._
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import java.io.FileOutputStream

import scala.collection.JavaConversions._


/** Adapter vår fasade til POI Workbook. */
class PoiAdapter(title: String, styleMap: Map[StyleName, Styles]) {
  private val workbook = new XSSFWorkbook
  private val sheet = workbook.createSheet(title)
  private val styles: Map[StyleName, CellStyle] = ??? /// HINT styleMap kan mappes vha createStyle

  printSetup

  /** Sett utskriftsoppsett. */
  private def printSetup {
    val printSetup: PrintSetup = sheet.getPrintSetup
    printSetup.setLandscape(true)
    sheet.setFitToPage(true)
    sheet.setHorizontallyCenter(true)
  }

  def create: Workbook = ??? /// HINT Fyll ut

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
  private def autoSizeSheet(noOfColumns: Int) {} /// HINT Fyll ut

  /** Legg til dataene. */
  def addData(values: SpreadSheet) = ???

  private def createRow(data: ValueMatrix[Int, Int, SheetCell], rowNum: Int, height: Option[Int]) {  } /// HINT Fyll ut
  private def createCell(sheetCell: Option[SheetCell], colNum: Int, row: Row) {  } /// HINT Fyll ut
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

