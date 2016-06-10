package no.mesan.fag.patterns.scala.timesheet.external.decorators

import no.mesan.fag.patterns.scala.timesheet.data.TimesheetEntry
import no.mesan.fag.patterns.scala.timesheet.external.TimeDataService

import java.util.logging.Logger
import org.joda.time.LocalDate

/** Dekoratør for vår eksterne tjeneste som sørger for at vi kun slår opp årsdata én gang. */
trait TimeDataServiceCachingDecorator extends TimeDataService {
  type PairOfYearFrom= (Int, Int)

  private val Log = Logger.getLogger("TimeDataServiceCachingDecorator")
  private var cache= Map[PairOfYearFrom, List[TimesheetEntry]]()
  private val thisYear= new LocalDate().getYear

  /** @{inheritDoc} Hvis angitt år er <= i år, prøv å hente fra cache. */
  /// HINT Overstyr forYear for å cache årsdata

/* /// HINT Nyttig?
  private def cacheLookup(year: Int, from: Int): List[TimesheetEntry] = {
    cache.get((year, from)) match {
      case Some(list)=>
        Log.info(s"Cache hit, not fetching for year=$year, from=$from")
        list
      case None =>
        val fetched = super.forYear(year, from)
        cache += (year, from) -> fetched
        fetched
    }
  }
  */
}
