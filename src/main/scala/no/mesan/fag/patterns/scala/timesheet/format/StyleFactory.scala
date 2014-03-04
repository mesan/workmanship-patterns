package no.mesan.fag.patterns.scala.timesheet.format

import org.apache.poi.ss.usermodel.{CellStyle, Workbook}

/** Tilgjengelige stiler. */
abstract sealed class StyleName
case object H1 extends StyleName
case object TableHead extends StyleName
case object TableHeadLeft extends StyleName
case object Col1 extends StyleName
case object ColN extends StyleName
case object Sums extends StyleName
case object Sum1 extends StyleName
case object Data extends StyleName

/** Oppretter stiler for et worksheet. */
object StyleFactory {

  /** Lag et stilbibliotek. */
  def styleSetup(wb: Workbook): Map[StyleName, CellStyle] =  createWbStyles(wb, styleSetup)

  private val styleSetup: Map[StyleName, Styles] = Map(
    H1 -> new Styles(true, false, 15, Some(ColorHighlight), None, None, None, None, None,
      HorizontalCenter, VerticalMiddle),
    TableHead -> new Styles(true, false, 12, Some(ColorHighlight), Some(ColorBg), Some(BorderThin),
      Some(BorderThin), Some(BorderThin), Some(BorderThin), HorizontalRight, VerticalMiddle),
    TableHeadLeft -> new Styles(true, true, 12, Some(ColorHighlight), Some(ColorBg), Some(BorderThin),
      Some(BorderThin), Some(BorderThin), Some(BorderMedium), HorizontalLeft, VerticalMiddle),
    Col1 -> new Styles(true, false, 10, None, None, Some(BorderThin), Some(BorderThin),
      Some(BorderThin), Some(BorderMedium), HorizontalLeft, VerticalBottom),
    Data -> new Styles(false, false, 10, None, None, Some(BorderThin), Some(BorderThin),
      Some(BorderThin), Some(BorderThin), HorizontalGen, VerticalBottom),
    ColN -> new Styles(false, true, 10, None, None, Some(BorderThin), Some(BorderThin), Some(BorderThin),
      Some(BorderMedium), HorizontalRight, VerticalBottom),
    Sums -> new Styles(false, true, 12, Some(ColorShadeFg), Some(ColorShadeBg), Some(BorderMedium),
      Some(BorderMedium), Some(BorderMedium), Some(BorderMedium), HorizontalGen, VerticalBottom),
    Sum1 -> new Styles(true, false, 12, Some(ColorShadeFg), Some(ColorShadeBg), Some(BorderMedium),
      Some(BorderMedium), Some(BorderMedium), Some(BorderMedium), HorizontalGen, VerticalBottom)
  )

  private def createWbStyles(wb: Workbook, map: Map[StyleName, Styles]): Map[StyleName, CellStyle] =
    map.map { case (name, spec) => name -> spec.createStyle(wb) }
}
