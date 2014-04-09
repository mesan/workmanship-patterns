package no.mesan.fag.patterns.scala.timesheet

import no.mesan.fag.patterns.scala.timesheet.external.TimeDataService
import no.mesan.fag.patterns.scala.timesheet.data.{TimesheetEntry, DoubleMatrix}

import org.apache.poi.ss.usermodel._

import org.joda.time.LocalDate

/** Timeliste for en enkelt bruker for en måned. */
class Timeliste(user: String, year: Int, month: Int, source: TimeDataService) extends Sheets {

  def createTimeliste: Workbook =
    generateReport(source, Timeliste.SheetTitle, "Aktivitet") { entry =>
      entry.when.year.get == year && entry.when.monthOfYear.get == month
    }

  override def retrieve(service: TimeDataService): Iterable[TimesheetEntry] =  service.forEmployee(user)

  override def dataExtraHeadings(matrix: DoubleMatrix): Unit =
    for (i<- 1 until new LocalDate(year, month, 1).dayOfMonth.getMaximumValue) matrix.ensureCol(dayRef(i))

  override def colRow(entry: TimesheetEntry): (String, String) =
    (dayRef(entry.when.getDayOfMonth), entry.activity.toString)

  override def headingTexts() = List(Timeliste.SheetTitle, user, s"$year", s"/ $month")

  private def dayRef(i: Int): String = f"$i%02d.$month%02d"
}

object Timeliste {
  val SheetTitle = "Timeliste"
}
