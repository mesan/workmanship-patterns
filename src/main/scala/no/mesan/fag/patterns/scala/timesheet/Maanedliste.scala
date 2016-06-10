package no.mesan.fag.patterns.scala.timesheet

import no.mesan.fag.patterns.scala.timesheet.external.TimeDataService
import no.mesan.fag.patterns.scala.timesheet.data.TimesheetEntry

import org.apache.poi.ss.usermodel.Workbook

/**
 * Hvem har fakturert på hvilke prosjekter i en gitt måned.
 * Aktiviteter >= 8000 ekskluderes.
 */
class Maanedliste(year: Int, month: Int, source: TimeDataService) extends Sheets {

  def createMaanedliste: Workbook =
    generateReport(source, Maanedliste.SheetName, "Bruker -- Aktivitet") {
      entry => entry.activity<Maanedliste.InternStart && entry.when.monthOfYear.get == month
    }
  override def retrieve(service: TimeDataService): Iterable[TimesheetEntry] =  service.forYear(year)
  override def colRow(entry: TimesheetEntry): (String, String) = (entry.activity.toString, entry.userID)
  override def headingTexts: List[String] = List(Maanedliste.SheetTitle, f"$year%04d/$month%02d")
}

object Maanedliste {
  val SheetName = "Maanedliste"
  val SheetTitle = "Månedsoppgjør"
  val InternStart = 8000
}