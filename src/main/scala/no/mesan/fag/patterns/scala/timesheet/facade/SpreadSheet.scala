package no.mesan.fag.patterns.scala.timesheet.facade

import scala.collection.mutable.{Map => MutableMap}
import no.mesan.fag.patterns.scala.timesheet.data.ValueMatrix

/** Holder data om et regneark uten å blande inn POI før til slutt. */
class SpreadSheet(val name:String) {
  /** Innholdet i arket. */
  val data = new ValueMatrix[Int, Int, SheetCell]()

  /** Noen rader skal ha tilpasset høyde. */
  private val rowHeigths: MutableMap[Int, Int] = MutableMap()

  /**
     * Sett radhøyde.
     * @param rowNo Hvilken rad
     * @param points Høyde i punkter
     * @return this
     */
    def setRowHeight(rowNo: Int, points: Int): SpreadSheet = {
      rowHeigths +=  rowNo -> points
      this
    }

    /**
     * Sett inn celle.
     * @param colNum Kolonne
     * @param rowNum Rad
     * @param cell Celle
     */
    def setCell(colNum: Int, rowNum: Int, cell: SheetCell) = { data.put(colNum, rowNum, cell) }

    /**
     * Hent høyde på rad.
     * @param rowNo Radnr.
     * @return Høyde i punkter, eller None hvis default
     */
    def rowHeight(rowNo: Int): Option[Int] = rowHeigths.get(rowNo)

    /** Hent siste radnummer. */
    def lastRowNum: Int =  data.rSize
}
