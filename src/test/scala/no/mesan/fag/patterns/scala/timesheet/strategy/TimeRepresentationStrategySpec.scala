package no.mesan.fag.patterns.scala.timesheet.strategy

import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class TimeRepresentationStrategySpec extends FlatSpec {

  "A TimeRepresentationDays" should "convert all values to days" in {
     val strategy = new TimeRepresentationDays {}
     assert(strategy.convertTime(0)===0.0D)
     assert(strategy.convertTime((60 * 7.5).asInstanceOf[Int])===1.0D)
     assert(strategy.convertTime((60 * 7.5 * 1.5).asInstanceOf[Int])===1.5)
  }

  "A TimeRepresentationHours" should "convert all values to closest hour" in {
    val strategy = new TimeRepresentationHours{}
    assert(strategy.convertTime(0)===0.0D)
    assert(strategy.convertTime(149)===2.0D)
    assert(strategy.convertTime(150)===3.0D)
    assert(strategy.convertTime(209)===3.0D)
    assert(strategy.convertTime(450)===8D)
  }

  "A TimeRepresentationHalfHours" should "convert all values down to half hours" in {
    val strategy = new TimeRepresentationHalfHours{}
    assert(strategy.convertTime(0)===0.0D)
    assert(strategy.convertTime(149)===2.0D)
    assert(strategy.convertTime(150)===2.5D)
    assert(strategy.convertTime(155)===2.5D)
  }

  "A TimeRepresentationMinutes" should "preserve all values" in {
    val strategy = new TimeRepresentationMinutes{}
    assert(strategy.convertTime(0)===0.0D)
    assert(strategy.convertTime(149)===149D)
    assert(strategy.convertTime(150)===150D)
    assert(strategy.convertTime(155)===155D)
  }
}
