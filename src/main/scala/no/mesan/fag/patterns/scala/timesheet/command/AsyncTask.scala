package no.mesan.fag.patterns.scala.timesheet.command

/** Pakker en oppgave som kan kjøre asynkront. */
trait AsyncTask {

  /** Presenter deg for verden. */
  def whoAmI: String

  /**
   * Run, rabbit, run.  Dig that hole, forget the sun.
   * And when at last the work is done. Don't sit down.
   * It's time to dig another one.
   */
  def executeTask()
}
