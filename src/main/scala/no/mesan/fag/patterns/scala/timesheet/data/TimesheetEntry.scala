package no.mesan.fag.patterns.scala.timesheet.data

import org.joda.time.LocalDate

/** 1 entry i timelisten. */
case class TimesheetEntry(userID: String, when: LocalDate, activity: Int, minutes: Int)

object TimesheetEntry {
  /**
   * Lag 1 eller flere forekomster fra strenger.  Ikke for den svake av hjertet.
   * @param entries n*(userID,when,activity,minutes)
   * @return Liste av innslag
   */
  def apply(entries: String*): List[TimesheetEntry] = apply(entries.toList)

  /**
   * Lag 1 eller flere forekomster fra strenger.  Ikke for den svake av hjertet.
   * @param entries n*(userID,when,activity,minutes)
   * @return Liste av innslag
   */
  def apply(entries: List[String]): List[TimesheetEntry] =
    { for ( list <- entries.grouped(4) )
        yield new TimesheetEntry(list(0), LocalDate.parse(list(1)), list(2).toInt, list(3).toInt) }.toList
}
