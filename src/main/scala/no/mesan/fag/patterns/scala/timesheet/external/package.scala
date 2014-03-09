package no.mesan.fag.patterns.scala.timesheet

import scala.language.implicitConversions
import no.mesan.fag.patterns.scala.timesheet.external.{TimeServiceIterator, TimeServiceAdapter, TimeDataService}

/** Implisitte funksjoner. */
package object external {
  /** Adapter til den eksterne tjenesten. */
  implicit def TimeDataServiceAdapter(source: TimeDataService): TimeServiceAdapter = new TimeServiceIterator(source)
}
