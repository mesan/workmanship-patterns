package no.mesan.fag.patterns.scala.timesheet.external.decorators

import no.mesan.fag.patterns.scala.timesheet.external.TimeDataService
import no.mesan.fag.patterns.scala.timesheet.data.TimesheetEntry

import java.util.logging.Logger

/** Dekoratør for den eksterne tjenesten som legger på litt fancy logging. */
trait TimeDataServiceLoggingDecorator extends TimeDataService {
  // Loggeren
  private var logger = Logger.getLogger("TimeDataServiceLoggingDecorator")

  protected[decorators] def setLogger(newLogger:Logger) { logger= newLogger }

  private def runWithTimer(intro:String)(f: => List[TimesheetEntry]): List[TimesheetEntry] = {
    val startsAt = System.nanoTime
    val result= f
    val endsAt= System.nanoTime
    logger.info(s"$intro\nFound ${result.size} entries in ${(endsAt-startsAt)/1e6}ms")
    result
  }

  abstract override def forEmployee(userID: String, from: Int): List[TimesheetEntry] =
    runWithTimer(s"forEmployee(userID=$userID, from=$from)"){ super.forEmployee(userID, from) }

  abstract override def forYear(year: Int, from: Int): List[TimesheetEntry] =
    runWithTimer(s"forYear(year=$year, from=$from)") { super.forYear(year, from) }
}
