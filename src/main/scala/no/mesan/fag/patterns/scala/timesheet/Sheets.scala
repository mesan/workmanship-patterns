package no.mesan.fag.patterns.scala.timesheet

import no.mesan.fag.patterns.scala.timesheet.external.{TimeDataServer, TimeSource}
import no.mesan.fag.patterns.scala.timesheet.data.TimesheetEntry
import no.mesan.fag.patterns.scala.timesheet.format.{RedTheme, ColorSpec, StyleName}

import java.io.FileOutputStream
import org.apache.poi.ss.usermodel._

/** Superklasse for timelister. */
abstract class Sheets {
  /**
   * Lag en cellereferanse (type A2B5) for en gitt kolonne+rad.
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
  ColorSpec.theme= RedTheme
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
