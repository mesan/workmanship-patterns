package no.mesan.fag.patterns.scala.timesheet.format

import org.apache.poi.ss.usermodel.{IndexedColors, CellStyle}

//////// Alignment
sealed abstract class Alignment(alignment:Int)

sealed abstract class Horizontal(val alignment:Int) extends Alignment(alignment)
case object HorizontalGen extends Horizontal(CellStyle.ALIGN_GENERAL)
case object HorizontalLeft extends Horizontal(CellStyle.ALIGN_LEFT)
case object HorizontalCenter extends Horizontal(CellStyle.ALIGN_CENTER)
case object HorizontalRight extends Horizontal(CellStyle.ALIGN_RIGHT)

sealed abstract class Vertical(val alignment:Int) extends Alignment(alignment)
case object VerticalTop extends Vertical(CellStyle.VERTICAL_TOP)
case object VerticalMiddle extends Vertical(CellStyle.VERTICAL_CENTER)
case object VerticalBottom extends Vertical(CellStyle.VERTICAL_BOTTOM)

//////// Borders
sealed abstract class BorderEdge
case object BorderTop extends BorderEdge
case object BorderBottom extends BorderEdge
case object BorderLeft extends BorderEdge
case object BorderRight extends BorderEdge

sealed abstract class BorderLine(val color:ColorSpec, val thickness:Int)
case object BorderMedium extends BorderLine(ColorDataGrid, CellStyle.BORDER_MEDIUM)
case object BorderThin extends BorderLine(ColorDataGrid, CellStyle.BORDER_THIN)
case object BorderThick extends BorderLine(ColorDataGrid, CellStyle.BORDER_THICK)

sealed case class BorderSpec(borderLine:BorderLine, borderEdge:BorderEdge)

// Themes
abstract class ThemeIndex
private case object Fg extends ThemeIndex
private case object Bg extends ThemeIndex
private case object High extends ThemeIndex
private case object ShadeB extends ThemeIndex
private case object ShadeF extends ThemeIndex
private case object Grid extends ThemeIndex

sealed abstract class Theme(var colors: Map[ThemeIndex, IndexedColors])
case object RedTheme extends Theme(Map(
  Fg->IndexedColors.BLACK, Bg->IndexedColors.WHITE, High->IndexedColors.DARK_RED,
  ShadeB->IndexedColors.DARK_RED, ShadeF->IndexedColors.WHITE,
  Grid->IndexedColors.BLACK))
case object BlueTheme extends Theme(Map(
  Fg->IndexedColors.BLACK, Bg->IndexedColors.WHITE, High->IndexedColors.DARK_BLUE,
  ShadeB->IndexedColors.DARK_BLUE, ShadeF->IndexedColors.WHITE,
  Grid->IndexedColors.BLACK))
case object GreenTheme extends Theme(Map(
  Fg->IndexedColors.BLACK, Bg->IndexedColors.WHITE, High->IndexedColors.DARK_GREEN,
  ShadeB->IndexedColors.DARK_GREEN, ShadeF->IndexedColors.WHITE,
  Grid->IndexedColors.BLACK))

//////// Colors
sealed abstract class ColorSpec(colorConst:ThemeIndex) {
  def color: Short = ColorSpec.theme.colors.getOrElse(colorConst, IndexedColors.BLACK).getIndex
}
case object ColorSpec {
  var theme: Theme= BlueTheme
}
case object ColorFg extends ColorSpec(Fg)
case object ColorBg extends ColorSpec(Bg)
case object ColorHighlight extends ColorSpec(High)
case object ColorShadeBg extends ColorSpec(ShadeB)
case object ColorShadeFg extends ColorSpec(ShadeF)
case object ColorDataGrid extends ColorSpec(Grid)
