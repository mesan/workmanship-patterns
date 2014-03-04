package no.mesan.fag.patterns.scala.timesheet.external

import no.mesan.fag.patterns.scala.timesheet.data.TimesheetEntry

/** Kilde til timedata. */
class SmallTimeSource extends Iterable[TimesheetEntry] {
  val Entries= TimesheetEntry(
    "A", "2014-01-01", "1000", "300",
    "A", "2014-01-01", "1000", "150",
    "A", "2014-01-02", "1000", "120",
    "A", "2014-01-02", "1001", "60",
    "B", "2014-01-02", "1001", "60",
    "C", "2014-02-01", "1000", "600")

  override def iterator: Iterator[TimesheetEntry] =  Entries.iterator
}
