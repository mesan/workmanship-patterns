package no.mesan.fag.patterns.scala.timesheet.facade

import no.mesan.fag.patterns.scala.timesheet.data.ValueMatrix

/** Holder data om et regneark uten å blande inn POI før til slutt. */
class SpreadSheet(val name:String) {
  /** Innholdet i arket. */
  private val data = new ValueMatrix[Integer, Integer, SheetCell]()
}
