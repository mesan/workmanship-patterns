package no.mesan.fag.patterns.scala.timesheet

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.FlatSpec

@RunWith(classOf[JUnitRunner])
class SheetSpec extends FlatSpec {
  class TestSheet extends Sheets
  val tst= new TestSheet

  "Cellref(1,x)" should "be row A" in {
    assert(tst.cellRef(1, 1)==="A1")
    assert(tst.cellRef(1, 100000000)==="A100000000")
  }

  "Cellref(26,x)" should "be row Z" in {
    assert(tst.cellRef(26, 2)==="Z2")
  }

  "Cellref(27,x)" should "be row AA" in {
    assert(tst.cellRef(27, 3)==="AA3")
  }

  "Cellref(53,x)" should "be row BA" in {
    assert(tst.cellRef(53, 4)==="BA4")
  }
}