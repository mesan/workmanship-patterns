package no.mesan.fag.patterns.scala.timesheet.facade

import no.mesan.fag.patterns.scala.timesheet.format.StyleName

import org.apache.poi.ss.usermodel.Cell

/** Rotklasse for "ting som skal i celler". */
abstract sealed class SheetCell(val style: Option[StyleName]) {

  /**
    * Sett inn verdien i cellen.
    * @ param cell Cellen
    * @ return Cellen
    */
 /// HINT   def fillCell(cell: Cell): Cell -- lag implementasjoner av denne i subklassene
}

/** En tom celle. */
class EmptyCell(style: StyleName) extends SheetCell(Some(style))

/** En celle med verdiinnhold (ikke formel). */
private[facade] abstract class ValueCell[T](val value: T, style: Option[StyleName]) extends SheetCell(style)

/** En celle med tekstinnhold. */
class StringCell(value: String, style: StyleName) extends ValueCell[String](value, Some(style))

/** En celle med En celle med tallinnhold. */
class DoubleCell(value: Double, style: StyleName) extends ValueCell[Double](value, Some(style))

/** Celle som inneholder en formel. */
class FormulaCell(val formula: String, style: StyleName) extends SheetCell(Some(style))

object SheetCell {
  /**
   * Lag en cellereferanse (type A2) for en gitt kolonne+rad.
   * @param col Kolonne
   * @param row Rad
   * @return Celleref
   */
  def cellRef(col: Int, row: Int): String = {
    val col0 = col - 1
    val ii = col0 / 26
    val i = col0 % 26
    val pfx: String = if (ii > 0) s"${('A' + ii - 1).asInstanceOf[Char]}" else ""
    s"${pfx}${('A'+i).asInstanceOf[Char]}$row"
  }


  /**
   * Lag en referanse til et celleområde (range, type A2:B5) for en gitt kolonne+rad.
   * @param fromCol Fra kolonne
   * @param fromRow Fra rad
   * @param toCol Til kolonne
   * @param toRow Til rad
   * @return Referanse til range
   */
  def rangeRef(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int): String =
    cellRef(fromCol, fromRow) + ":" + cellRef(toCol, toRow)

  /** Opprett en SUM-celle. */
  def formulaSUM(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int, style: StyleName): FormulaCell =
    new FormulaCell(s"SUM(${rangeRef(fromCol, fromRow, toCol, toRow)})", style)
}