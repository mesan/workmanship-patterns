package no.mesan.fag.patterns.scala.timesheet.strategy

/** Interface for hvordan tid skal vises i ferdig xlsx. */
trait TimeRepresentationStrategy {
  /**
   * Konverterer antall minutter til riktig visning.
   * @param minutes minutter registrert
   * @return visningsverdi for xlsx
   */
  def convertTime(minutes: Int): Double
}

/** Konverterer minutter til timer (halvtimes oppløsning) -- default. */
trait TimeRepresentationHalfHours extends TimeRepresentationStrategy {
  override def convertTime(minutes: Int): Double = (minutes / 30) / 2.0D
}

/// HINT  Lag tilsvarende for hele dager, hele timer og originalverdien i minutter.




