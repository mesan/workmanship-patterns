package no.mesan.fag.patterns.scala.timesheet

import no.mesan.fag.patterns.scala.timesheet.data.{TimesheetEntry, DoubleMatrix}
import no.mesan.fag.patterns.scala.timesheet.external.{TimeIteratorService, TimeDataService}

import org.apache.poi.ss.usermodel.Workbook

/** Timer per prosjekt per måned over et år. */
class Aarsliste(year: Int, source: TimeDataService) extends Sheets {
  def createAarsoversikt: Workbook = generateReport(source, Aarsliste.SheetTitle, "Aktivitet -- Måned") { entry=>true }
  override def retrieve(service: TimeIteratorService): Iterable[TimesheetEntry] =  service.forYear(year)
  override def dataExtraHeadings(matrix: DoubleMatrix) = for (i<- 1 until 12) matrix.ensureCol(f"$i%02d")
  override def headingTexts: List[String] = List(Aarsliste.SheetTitle, f"$year%04d")
  override def colRow(entry: TimesheetEntry): (String, String) =
    (f"${entry.when.getMonthOfYear}%02d", entry.activity.toString)
}

object Aarsliste {
  val SheetTitle= "Årsliste"
}
