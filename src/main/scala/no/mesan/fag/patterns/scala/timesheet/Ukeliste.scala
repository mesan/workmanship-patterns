package no.mesan.fag.patterns.scala.timesheet

import no.mesan.fag.patterns.scala.timesheet.external.{TimeIteratorService, TimeDataService}
import no.mesan.fag.patterns.scala.timesheet.data.{DoubleMatrix, TimesheetEntry}

import org.apache.poi.ss.usermodel.Workbook
import org.joda.time.{DateTimeConstants, LocalDate}

/** Timeliste for alle brukere for en enkelt uke. */
class Ukeliste(year: Int, month: Int, day: Int, source: TimeDataService) extends Sheets {
  private val Days = Array("Man", "Tir", "Ons", "Tor", "Fre", "Lør", "Søn")
  private val (fromDate, toDate) = {
    val date= new LocalDate(year, month, day)
    (date.withDayOfWeek(DateTimeConstants.MONDAY),date.withDayOfWeek(DateTimeConstants.SUNDAY))
  }

  def createUkeliste: Workbook =
    generateReport(source, Ukeliste.SheetTitle, "Aktivitet/bruker -- dag", sortedCols = false) { entry =>
      !(entry.when.isBefore(fromDate) || entry.when.isAfter(toDate))
    }

  override def retrieve(service: TimeIteratorService): Iterable[TimesheetEntry] =  service.forYear(year)

  override def dataExtraHeadings(matrix: DoubleMatrix) = for (day <-Days) matrix.ensureCol(day)

  override def colRow(entry: TimesheetEntry): (String, String)=
    (Days(entry.when.getDayOfWeek-1), s"${entry.activity} / ${entry.userID}")

  override def headingTexts: List[String] =
    List(Ukeliste.SheetTitle, fromDate.toString("yyyyMMdd"),"-",toDate.toString("yyyyMMdd"))
}

object Ukeliste {
  var SheetTitle= "Ukeliste"
}