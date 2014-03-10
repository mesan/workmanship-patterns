package no.mesan.fag.patterns.scala.timesheet.strategy

/** Interface for hvordan tid skal vises i ferdig xlsx. */
trait TimeRepresentationStrategy {
  /**
   * Konverterer antall minutter til riktig visning.
   * @param minutes minutter registrert
   * @return visningsverdi for xlsx
   */
  def convert(minutes: Int): Double
}

/** Konverterer minutter til timer (halvtimes oppløsning) -- default. */
class TimeRepresentationHalfHours extends TimeRepresentationStrategy {
  override def convert(minutes: Int): Double = (minutes / 30) / 2.0D
}

/** Konverterer minutter til nærmeste hele timer. */
class TimeRepresentationHours extends TimeRepresentationStrategy {
  override def convert(minutes: Int): Double = math.round(minutes.asInstanceOf[Double] / 60D)
}

/** Konverterer minutter til hele dagsverk. */
class TimeRepresentationDays extends TimeRepresentationStrategy {
  override def convert(minutes: Int): Double = minutes.asInstanceOf[Double] / (60D * 7.5D)
}

/** Gjør ingen konvertering, returnerer antall minutter uendret. */
class TimeRepresentationMinutes extends TimeRepresentationStrategy {
  override def convert(minutes: Int): Double = minutes
}