package no.mesan.fag.patterns.scala

import scala.language.implicitConversions
import no.mesan.fag.patterns.scala.timesheet.external.{TimeServiceIterator, TimeServiceAdapter, TimeDataService}

/** Implisitte funksjoner. */
package object timesheet {
  /** Adapter til den eksterne tjenesten. */
  implicit def TimeDataServiceAdapter(source: TimeDataService): TimeServiceAdapter = new TimeServiceIterator(source)
}
