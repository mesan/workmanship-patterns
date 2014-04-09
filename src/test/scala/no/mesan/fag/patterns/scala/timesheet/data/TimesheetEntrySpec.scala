package no.mesan.fag.patterns.scala.timesheet.data

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec

@RunWith(classOf[JUnitRunner])
class TimesheetEntrySpec extends FlatSpec {

  val entries: List[TimesheetEntry] = TimesheetEntry(
    "larsr", "2014-01-06", "1000", "30",
    "larsr", "2014-01-06", "2134", "420",
    "larsr", "2014-01-07", "2134", "540",
    "larsr", "2014-01-07", "1001", "30",
    "larsr", "2014-01-08", "2134", "450")

  "A timesheetentry" should "create entries from 4 strings" in {
      assert(entries.size===5)
  }

  it should "contain correct data" in {
    for (entry <- entries) {
      assert("larsr"===entry.userID)
      assert(entry.when.year.get===2014)
    }
    assert(entries(2).activity===2134)
  }
}
