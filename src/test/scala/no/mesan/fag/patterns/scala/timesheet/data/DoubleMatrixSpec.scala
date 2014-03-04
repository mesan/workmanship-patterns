package no.mesan.fag.patterns.scala.timesheet.data

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class DoubleMatrixSpec extends ValueMatrixSpec {

  override def create=  new DoubleMatrix

  "Add to empty cell" should "equivalent to put" in {
    val m = create.add("C", "R", 5D)
    assert(m.get("C", "R")===Some(5D))
  }

  "Add to nonempty cell" should "add value" in {
    val m = create.add("C", "R", 5D).add("C", "R", 10D)
    assert(m.get("C", "R")===Some(15D))
    assert(m.rSize===1)
  }
}