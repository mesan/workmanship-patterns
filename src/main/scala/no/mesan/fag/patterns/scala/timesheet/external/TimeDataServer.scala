package no.mesan.fag.patterns.scala.timesheet.external

import scala.language.postfixOps
import no.mesan.fag.patterns.scala.timesheet.data.TimesheetEntry

/** Dette er liksom selve den eksterne serveren. Tilgi meg, hadde ikke råd til en stormaskin til hver. */
class TimeDataServer(src: Iterable[TimesheetEntry]) extends TimeDataService {

  override def forEmployee(userID: String, from: Int): List[TimesheetEntry] =
    (src filter (_.userID == userID)).slice(from, from + BatchSize) toList

  override def forYear(year: Int, from: Int): List[TimesheetEntry] =
    (src filter (_.when.getYear == year)).slice(from, from + BatchSize) toList
}
