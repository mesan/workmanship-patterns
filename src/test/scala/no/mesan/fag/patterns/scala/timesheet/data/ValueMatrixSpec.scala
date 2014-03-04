package no.mesan.fag.patterns.scala.timesheet.data

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ValueMatrixSpec extends FlatSpec {

  def create: ValueMatrix[String, String, Double] =  new ValueMatrix[String, String, Double]()

  "An empty matrix" should "have size 0" in {
    val m = create
    assert(m.rowKeys().size===0)
    assert(m.colKeys().size===0)
    assert(m.rSize===0)
    assert(m.cSize===0)
  }
  it should "return null for any reference" in {
    val m= create
    assert(m.get("A", "1")===None)
    assert(m.get("B", "1")===None)
  }

  "A matrix with one entry" should "have the right size" in {
    val m= create.put("A", "1", 4.0D)
    assert(m.rowKeys().size===1)
    assert(m.colKeys().size===1)
    assert(m.rSize===1)
    assert(m.cSize===1)
  }

  it should "find (only) inserted values" in {
    val m= create.put("A", "1", 4.0D)
    assert(m.get("A", "1")===Some(4.0D))
    assert(m.get("A", "2")===None)
  }

  "put" should "overwrite existing values, if any" in {
      val m= create.put("A", "1", 4.0D).put("A", "2", 0.0D)
        /*Overwrite:*/.put("A", "1", -10.0D)
      assert(m.colKeys().contains("A"))
      assert(m.rowKeys().contains("1"))
      assert(m.rSize===2)
      assert(m.cSize===1)
      assert(m.get("A", "1")===Some(-10.0D))
    }

  "ensureRow" should "NOT overwrite values" in {
      val m= create.put("A", "1", 4.0D)
      m.ensureRow("1")
      assert(m.rowKeys().contains("1"))
      assert(m.rSize===1)
    }

  "ensureCols" should "NOT overwrite values" in {
    val m= create.put("A", "1", 4.0D)
    m.ensureCol("A")
    assert(m.colKeys().contains("A"))
    assert(m.cSize=== 1)
  }

  "larger matrix" should "have correct contents" in {
    val m = create56
    assert(m.rSize===5)
    assert(m.cSize===6)
    for (r <- 1 to 5) assert(m.rowKeys() contains("R" + 2 * r))
    for (c <- 1 to 6) assert(m.colKeys() contains("C" + c))
    assert(m.get("C3", "R4")===Some(24.0D))
    assert(m.get("C1", "R5")===None)
  }

  it should "be able to sort row keys" in {
    val keys= create56.rowKeys(sorted = true)
    var res= List[String]()
    res ::=  "R10"
    for (r <- 1 to 4)  res ::= ("R" + (2*r))
    assert(keys===res.reverse)
  }

  it should "be able to sort column keys" in {
    val keys = create56.colKeys(sorted = true)
    var res= List[String]()
    for (c <- 1 to 6)  res ::= ("C" + c)
    assert(keys===res.reverse)
  }

  "has" should "only find existing values" in {
    val m= create.put("A", "1", 4.0D)
    assert(m.has("A", "1"))
    assert(!m.has("1", "A"))
  }

  "Different index types" should "be supported" in {
    val m = new ValueMatrix[Integer, Double, String]().put(4, 3.5D, "Test")
    assert(m.has(4, 3.5D))
    assert(m.get(-4, -3.5D)===None)
    assert(m.rSize===1)
    assert(m.colKeys().contains(4))
  }

  private def create56: ValueMatrix[String, String, Double] = {
    val m: ValueMatrix[String, String, Double] = create
    for (r <- 5 to 1 by -1;
         c <- 6 to 1 by -1) m.put("C" + c, "R" + (2*r), 4.0D * r * c)
    m
  }
}