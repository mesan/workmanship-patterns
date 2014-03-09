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

class TimeServiceIterator(source: TimeDataService) extends TimeServiceAdapter {
  private class EntryIterator(fillFunction: Int=>List[TimesheetEntry]) extends Iterator[TimesheetEntry] {
    var buffer: List[TimesheetEntry] = Nil
    var fetched = 0
    var lastIsFetched = false

    override def hasNext: Boolean = {
      refillIfNeeded
      !buffer.isEmpty
    }

    private def refillIfNeeded {
      if (!buffer.isEmpty || lastIsFetched) return
      val newBatch: List[TimesheetEntry] = fillFunction(fetched)
      fetched += newBatch.size
      buffer ++= newBatch
      lastIsFetched = newBatch.isEmpty
    }

    override def next(): TimesheetEntry = {
      refillIfNeeded
      if (buffer.isEmpty) throw new NoSuchElementException
      val entry= buffer.head
      buffer= buffer.tail
      entry
    }
  }

  override def forEmployee(userID: String): Iterable[TimesheetEntry] =
    new EntryIterator(fetched => source.forEmployee(userID, fetched)).toIterable

  override def forYear(year: Int): Iterable[TimesheetEntry]=
    new EntryIterator(fetched => source.forYear(year, fetched)).toIterable
}
