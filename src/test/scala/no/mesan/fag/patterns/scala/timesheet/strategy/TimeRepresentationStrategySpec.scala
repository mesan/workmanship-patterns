package no.mesan.fag.patterns.scala.timesheet.strategy

import no.mesan.fag.patterns.scala.timesheet.external.{SmallTimeSource, TimeDataServer}
import org.apache.poi.ss.usermodel.{Cell, Sheet}

import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class TimeRepresentationStrategySpec extends FlatSpec {

  "A TimeRepresentationDays" should "convert all values to days" in {
     val strategy = new TimeRepresentationDays
     assert(strategy.convert(0)===0.0D)
     assert(strategy.convert((60 * 7.5).asInstanceOf[Int])===1.0D)
     assert(strategy.convert((60 * 7.5 * 1.5).asInstanceOf[Int])===1.5)
  }

  "A TimeRepresentationHours" should "convert all values to closest hour" in {
    val strategy = new TimeRepresentationHours
    assert(strategy.convert(0)===0.0D)
    assert(strategy.convert(149)===2.0D)
    assert(strategy.convert(150)===3.0D)
    assert(strategy.convert(209)===3.0D)
    assert(strategy.convert(450)===8D)
  }

  "A TimeRepresentationHalfHours" should "convert all values down to half hours" in {
    val strategy = new TimeRepresentationHalfHours
    assert(strategy.convert(0)===0.0D)
    assert(strategy.convert(149)===2.0D)
    assert(strategy.convert(150)===2.5D)
    assert(strategy.convert(155)===2.5D)
  }

  "A TimeRepresentationMinutes" should "preserve all values" in {
    val strategy = new TimeRepresentationMinutes
    assert(strategy.convert(0)===0.0D)
    assert(strategy.convert(149)===149D)
    assert(strategy.convert(150)===150D)
    assert(strategy.convert(155)===155D)
  }
}