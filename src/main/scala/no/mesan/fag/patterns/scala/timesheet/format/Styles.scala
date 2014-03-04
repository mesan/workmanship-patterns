package no.mesan.fag.patterns.scala.timesheet.format

import org.apache.poi.ss.usermodel.{Font, Workbook, CellStyle}

/** Holder spesifikasjoner for en stil. */
class Styles (val bold: Boolean, var italic: Boolean, var points: Int,
              val fgColor: Option[ColorSpec], val bgColor: Option[ColorSpec],
              val borderTop: Option[BorderLine],  val borderBottom: Option[BorderLine],
              val borderLeft: Option[BorderLine], val borderRight: Option[BorderLine],
              val horizontal: Horizontal, val vertical: Vertical) {


  /** Oversett spesifkasjon til Excel-stil. */
  def createStyle(wb: Workbook): CellStyle = {
    val style: CellStyle = wb.createCellStyle
    makeFont(wb, style)
    bgColor map { color =>
      style.setFillForegroundColor(color.color)
      style.setFillPattern(CellStyle.SOLID_FOREGROUND)
    }
    style.setAlignment(horizontal.alignment.asInstanceOf[Short])
    style.setVerticalAlignment(vertical.alignment.asInstanceOf[Short])
    borderTop map {b =>    style.setBorderTop(b.thickness.asInstanceOf[Short])
                           style.setTopBorderColor(b.color.color)}
    borderBottom map {b => style.setBorderBottom(b.thickness.asInstanceOf[Short])
                           style.setBottomBorderColor(b.color.color)}
    borderLeft map {b =>   style.setBorderLeft(b.thickness.asInstanceOf[Short])
                           style.setLeftBorderColor(b.color.color)}
    borderRight map {b =>  style.setBorderRight(b.thickness.asInstanceOf[Short])
                           style.setLeftBorderColor(b.color.color)}
    style
  }

  private def makeFont(wb: Workbook, style: CellStyle) {
      val font= wb.createFont
      font.setFontHeightInPoints(points.asInstanceOf[Short])
      fgColor map { color=> font.setColor(color.color) }
      if (bold) font.setBoldweight(Font.BOLDWEIGHT_BOLD)
      if (italic) font.setItalic(true)
      style.setFont(font)
  }
}
