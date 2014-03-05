package no.mesan.fag.patterns.scala.timesheet.external

import no.mesan.fag.patterns.scala.timesheet.data.TimesheetEntry

/** Adapter til den eksterne tjenesten. */
trait TimeServiceAdapter {

  /**
   * Henter alle timene registrert for en gitt bruker.
   *
   * @param userID Brukeren vi vil hente timer for
   * @return Liste av matchende timeføringer
   */
  def forEmployee(userID: String): Iterable[TimesheetEntry]

  /**
   * Henter alle timene registrert for et gitt år.
   *
   * @param year Året vi vil hente for
   * @return Liste av matchende timeføringer
   */
  def forYear(year: Int): Iterable[TimesheetEntry]
}
