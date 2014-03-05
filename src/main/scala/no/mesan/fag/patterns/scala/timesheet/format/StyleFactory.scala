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

  private val styleSetup: Map[StyleName, Styles] = {
    val h1= Styles(bold=true, points=15, fgColor=Some(ColorHighlight), horizontal=HorizontalCenter, vertical=VerticalMiddle)
    val tblHead= h1.copy(points=12, bgColor=Some(ColorBg), horizontal=HorizontalRight).allBorders(Some(BorderThin))
    val data = Styles().allBorders(Some(BorderThin))
    val shaded = Styles(points = 12).allBorders(Some(BorderMedium)).shaded()
    Map(
      H1 -> h1,
      TableHead -> tblHead,
      TableHeadLeft -> tblHead.copy(italic=true, borderRight=Some(BorderMedium), horizontal=HorizontalLeft),
      Data -> data,
      Col1 -> data.copy(bold=true, borderRight=Some(BorderMedium), horizontal=HorizontalLeft),
      ColN -> data.copy(italic=true, borderRight=Some(BorderMedium), horizontal=HorizontalRight),
      Sums -> shaded.copy(italic=true),
      Sum1 -> shaded.copy(bold=true)
    )
  }

  private def createWbStyles(wb: Workbook, map: Map[StyleName, Styles]): Map[StyleName, CellStyle] =
    map.map { case (name, spec) => name -> spec.createStyle(wb) }
}
