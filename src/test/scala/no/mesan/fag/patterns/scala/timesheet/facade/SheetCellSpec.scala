package no.mesan.fag.patterns.scala.timesheet.facade

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.FlatSpec

@RunWith(classOf[JUnitRunner])
class SheetCellSpec extends FlatSpec {

  "Cellref(1,x)" should "be row A" in {
    assert(SheetCell.cellRef(1, 1)==="A1")
    assert(SheetCell.cellRef(1, 100000000)==="A100000000")
  }

  "Cellref(26,x)" should "be row Z" in {
    assert(SheetCell.cellRef(26, 2)==="Z2")
  }

  "Cellref(27,x)" should "be row AA" in {
    assert(SheetCell.cellRef(27, 3)==="AA3")
  }

  "Cellref(53,x)" should "be row BA" in {
    assert(SheetCell.cellRef(53, 4)==="BA4")
  }
}
