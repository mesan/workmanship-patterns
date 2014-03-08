package no.mesan.fag.patterns.scala.timesheet

import no.mesan.fag.patterns.scala.timesheet.external.{SmallTimeSource, TimeDataServer}
import org.apache.poi.ss.usermodel.{Cell, Sheet}

import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class MaanedlisteSpec extends FlatSpec {
  val timeliste= new Maanedliste(2014, 1, new TimeDataServer(new SmallTimeSource))
  val wb = timeliste.createMaanedliste
  val title = Maanedliste.SheetName

  "A new MÅNEDSLISTE workbook" should "be created" in {
    assert(wb!==null)
  }

  it should "contain one sheet" in {
    assert(wb.getNumberOfSheets===1)
  }

  it should "have a correctly named sheet" in {
    val sheet= wb.getSheet(title)
    assert(sheet!==null)
  }

  it should "contain the correct number of rows and columns" in {
    val sheet= wb.getSheet(title)
    val lastRowNum: Int = sheet.getLastRowNum
    assert(lastRowNum===5)
    assert(sheet.getRow(lastRowNum).getLastCellNum===4)
  }

  "The workbook" should "have correct title" in {
    val sheet = wb.getSheet(title)
    assert(extractCell(sheet, 0, 0).getStringCellValue===Maanedliste.SheetTitle)
  }

  it should "have a correct sample data value" in {
    val sheet= wb.getSheet(title)
    assert(extractCell(sheet, 2, 3).getNumericCellValue===9.5D)
  }

  it should "have a correct sum cell" in {
    val sheet= wb.getSheet(title)
    val cell = extractCell(sheet, 1, 5)
    assert(cell.getNumericCellValue===11.5D)
    assert(cell.getCellStyle.getFillForegroundColor===18)
  }

  private def extractCell(sheet: Sheet, cellnum: Int, rownum: Int): Cell =  sheet.getRow(rownum).getCell(cellnum)
}