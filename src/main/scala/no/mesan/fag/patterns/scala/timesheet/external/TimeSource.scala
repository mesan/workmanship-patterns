package no.mesan.fag.patterns.scala.timesheet.external

import no.mesan.fag.patterns.scala.timesheet.data.TimesheetEntry
import scala.io.Source

/** Kilde til timedata. */
object TimeSource extends Iterable[TimesheetEntry] {

  private def getContents(fileName:String): List[String] = {
    val source = Source.fromFile(fileName, "ISO-8859-1")
    for (l <- source.getLines().toList; s<- l.split(" +")) yield s
  }

  val Entries = TimesheetEntry(getContents("src/main/resources/timer.txt"))
  override def iterator: Iterator[TimesheetEntry] =  Entries.iterator
}
