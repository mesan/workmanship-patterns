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

/** Konverterer minutter til nærmeste hele timer. */
trait TimeRepresentationHours extends TimeRepresentationStrategy {
  override def convertTime(minutes: Int): Double = math.round(minutes.asInstanceOf[Double] / 60D)
}

/** Konverterer minutter til hele dagsverk. */
trait TimeRepresentationDays extends TimeRepresentationStrategy {
  override def convertTime(minutes: Int): Double = minutes.asInstanceOf[Double] / (60D * 7.5D)
}

/** Gjør ingen konvertering, returnerer antall minutter uendret. */
trait TimeRepresentationMinutes extends TimeRepresentationStrategy {
  override def convertTime(minutes: Int): Double = minutes
}
