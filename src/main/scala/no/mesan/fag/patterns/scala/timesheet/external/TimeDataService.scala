package no.mesan.fag.patterns.scala.timesheet.external

import no.mesan.fag.patterns.scala.timesheet.data.TimesheetEntry

/** Grensesnittet til den eksterne tjenesten. */
trait TimeDataService {

  /** Antall som hentes av gangen. */
  val BatchSize = 25

  /**
   * Henter alle timene registrert for en gitt bruker.
   * Hensikten var at denne skulle filtrere bort alle fakturererte timer, slik at den ikke vokser uhemmet over tid,
   * men det virker visst ikke...
   *
   * @param from 0 første gang, deretter antall hentet tidligere
   * @param userID Brukeren vi vil hente timer for
   * @return Liste av matchende timeføringer; dersom den returnerer færre en BATCH_SIZE forekomster er alle hentet
   */
  def forEmployee(userID: String, from: Int): List[TimesheetEntry]

  /**
   * Henter alle timene registrert for et gitt år.
   *
   * @param from 0 første gang, deretter antall hentet tidligere
   * @param year Året vi vil hente for
   * @return Liste av matchende timeføringer; dersom den returnerer færre en BATCH_SIZE forekomster er alle hentet
   */
  def forYear(year: Int, from: Int): List[TimesheetEntry]
}
