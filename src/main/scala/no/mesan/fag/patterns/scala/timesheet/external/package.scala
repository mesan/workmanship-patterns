package no.mesan.fag.patterns.scala.timesheet

import scala.language.implicitConversions

/** Implisitte funksjoner. */
package object external {
  /** Adapter til den eksterne tjenesten. */
  implicit def TimeDataServiceAdapter(source: TimeDataService): TimeServiceAdapter = new TimeServiceIterator(source)
}
