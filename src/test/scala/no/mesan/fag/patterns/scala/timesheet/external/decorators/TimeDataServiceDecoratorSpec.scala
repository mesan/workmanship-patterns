package no.mesan.fag.patterns.scala.timesheet.external.decorators

import no.mesan.fag.patterns.scala.timesheet.external.{TimeDataService, TimeDataServer}
import no.mesan.fag.patterns.scala.timesheet.data.TimesheetEntry

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.scalatest.mock.MockitoSugar

import org.joda.time.LocalDate
import java.util.logging.Logger

/** Tester for kombinering av dekoratører. */
@RunWith(classOf[JUnitRunner])
class TimeDataServiceDecoratorSpec extends FlatSpec with MockitoSugar {
  private val entries1= TimesheetEntry("osk", "2013-07-30", "1030", "60")
  private val entries2= TimesheetEntry("osk", "2014-07-30", "1030", "60", "osk", "2014-07-31", "1030", "60")
  private val currentYear = new LocalDate().getYear

  abstract class DelegatingTimeDataService(val mock: TimeDataService) extends TimeDataService {
    def forEmployee(userID: String, from: Int): List[TimesheetEntry] = mock.forEmployee(userID, from)
    def forYear(year: Int, from: Int): List[TimesheetEntry] = mock.forYear(year, from)
  }

  "The decorators" should "be possible to combine" in {
    val loggingAndCachingDecoratedService=
      new TimeDataServer(entries1) with TimeDataServiceLoggingDecorator with TimeDataServiceCachingDecorator
    for (timesheetEntry <- loggingAndCachingDecoratedService.forYear(2013)) assert(timesheetEntry!==null)
    for (timesheetEntry <- loggingAndCachingDecoratedService.forYear(2013)) assert(timesheetEntry!==null)
  }

  trait SetupForCaching {
    val service = new DelegatingTimeDataService(mock[TimeDataService]) with TimeDataServiceCachingDecorator
    when(service.mock.forYear(anyInt, anyInt)).thenReturn(entries2)
  }

  "The TimeDataServiceCachingDecorator" should "always fetch from service for current year" in {
    new SetupForCaching {
      service.forYear(currentYear, 0)
      service.forYear(currentYear, 0)
      service.forYear(currentYear, 0)
      verify(service.mock, times(3)).forYear(currentYear, 0)
    }
  }

  it should "only fetch from service the first time for last year" in {
    new SetupForCaching {
      val lastYear= currentYear - 1
      service.forYear(lastYear, 0)
      service.forYear(lastYear, 0)
      service.forYear(lastYear, 0)
      verify(service.mock, times(1)).forYear(lastYear, 0)
    }
  }

  trait SetupForLogging {
    val mockLog= mock[Logger]
    val service = new DelegatingTimeDataService(mock[TimeDataService]) with TimeDataServiceLoggingDecorator
    service.setLogger(mockLog)
    when(service.mock.forEmployee(anyString(), anyInt())).thenReturn(entries2)
    when(service.mock.forYear(anyInt(), anyInt())).thenReturn(entries2)
  }

  "A TimeDataServiceLoggingDecorator" should "log for employee" in {
    new SetupForLogging {
      service.forEmployee("osk", 0)
      verify(mockLog, times(1)).info(any())
    }
  }

  it should "log for year" in {
    new SetupForLogging { service.forYear(2014, 0) }
  }
}
