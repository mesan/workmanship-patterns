package no.mesan.fag.patterns.scala.timesheet.format

import org.apache.poi.ss.usermodel.{Font, Workbook, CellStyle}

/** Holder spesifikasjoner for en stil. */
/// HINT Gjør om dette til en case class med fornuftige defaults
class Styles (val bold: Boolean, var italic: Boolean, var points: Int,
              val fgColor: Option[ColorSpec], val bgColor: Option[ColorSpec],
              val borderTop: Option[BorderLine],  val borderBottom: Option[BorderLine],
              val borderLeft: Option[BorderLine], val borderRight: Option[BorderLine],
              val horizontal: Horizontal, val vertical: Vertical) {


  /** Oversett spesifkasjon til Excel-stil. */
  def createStyle(wb: Workbook): CellStyle = {
    val style: CellStyle = wb.createCellStyle
    makeFont(wb, style)
    bgColor foreach { color =>
      style.setFillForegroundColor(color.color)
      style.setFillPattern(CellStyle.SOLID_FOREGROUND)
    }
    style.setAlignment(horizontal.alignment.asInstanceOf[Short])
    style.setVerticalAlignment(vertical.alignment.asInstanceOf[Short])
    borderTop foreach { b =>    style.setBorderTop(b.thickness.asInstanceOf[Short])
                           style.setTopBorderColor(b.color.color)}
    borderBottom foreach { b => style.setBorderBottom(b.thickness.asInstanceOf[Short])
                           style.setBottomBorderColor(b.color.color)}
    borderLeft foreach { b =>   style.setBorderLeft(b.thickness.asInstanceOf[Short])
                           style.setLeftBorderColor(b.color.color)}
    borderRight foreach { b =>  style.setBorderRight(b.thickness.asInstanceOf[Short])
                           style.setLeftBorderColor(b.color.color)}
    style
  }

  private def makeFont(wb: Workbook, style: CellStyle) {
      val font= wb.createFont
      font.setFontHeightInPoints(points.asInstanceOf[Short])
      fgColor foreach { color=> font.setColor(color.color) }
      if (bold) font.setBoldweight(Font.BOLDWEIGHT_BOLD)
      if (italic) font.setItalic(true)
      style.setFont(font)
  }
}
