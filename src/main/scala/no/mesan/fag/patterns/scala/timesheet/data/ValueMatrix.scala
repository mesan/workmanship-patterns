package no.mesan.fag.patterns.scala.timesheet.data

import scala.collection.mutable.{LinkedHashSet => MutableSet}

/**
 * A sort of sparse, associative, two-dimensional array.
 * Note: not thread-safe, several methods need some sort of synchronization to achieve that.
 *
 * C: The column type
 * R: The row type
 * V: The value types to keep
 * @author lre
 */
class ValueMatrix[C <% Comparable[C], R <% Comparable[R], V] {
  /** Used to separate row & column in the storage. Inspired by AWK. */
  private val ARRAYSEP = 1.toChar.toString
  /** Keep track of row keys. */
  private val allRowKeys = MutableSet[R]()
  /** Keep track of column keys. */
  private val allColKeys = MutableSet[C]()
  /** The real values. */
  private var values: Map[String, V] = Map()

  /** Create styleMap key. */
  private def key(col: C, row: R)=  col + ARRAYSEP + row

  /** Create a possibly sorted list from a key set. */
  private def keyList[T <% Comparable[T]](sorted: Boolean, orgKeys: MutableSet[T]): List[T] =
    if (sorted) orgKeys.toList.sorted
    else orgKeys.toList

  /** Return list of row keys, possibly sorted. */
  def rowKeys(sorted: Boolean=false): List[R] =  keyList(sorted, allRowKeys)
  /** Return list of column keys, possibly sorted. */
  def colKeys(sorted: Boolean=false): List[C] =  keyList(sorted, this.allColKeys)

  /** Get the number of rows. */
  def rSize: Int =  this.allRowKeys.size
  /** Get the number of columns. */
  def cSize: Int =  this.allColKeys.size

  /** Get the value of a given cell (null if not defined). */
  def get(col: C, row: R): Option[V] =  values.get(key(col, row))

  /** Add a value, overwriting whatever was there. */
  def put(col: C, row: R, value: V): ValueMatrix[C, R, V] = {
    allColKeys += col
    allRowKeys += row
    values += (key(col, row) -> value)
    this
  }

  /** Check if cell has value. */
  def has(col: C, row: R): Boolean =  get(col, row).isDefined

  /** Sometimes you need certain rows, regardless of whether they actually exist in data. */
  def ensureRow(rows: R*): ValueMatrix[C, R, V] = {
    allRowKeys ++= rows
    this
  }
  /** Sometimes you need certain columns, regardless of whether they actually exist in data. */
  def ensureCol(cols: C*): ValueMatrix[C, R, V] = {
    allColKeys ++= cols
    this
  }

  override def toString:String = s"ValueMatrix{cols=$cSize rows=$rSize values=$values.size}"
}
