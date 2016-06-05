package no.mesan.fag.patterns.scala.timesheet.format

import org.apache.poi.ss.usermodel.{Font, Workbook, CellStyle}

/** Holder spesifikasjoner for en stil. */
case class Styles (bold: Boolean =false, italic: Boolean =false, points: Int =10,
                   fgColor: Option[ColorSpec] =Some(ColorFg), bgColor: Option[ColorSpec] =None,
                   borderTop: Option[BorderLine] =None,  borderBottom: Option[BorderLine] =None,
                   borderLeft: Option[BorderLine] =None, borderRight: Option[BorderLine] =None,
                   horizontal: Horizontal= HorizontalGen, vertical: Vertical =VerticalBottom) {

  /**
   * Lag en kopi med alle kantlinjer satt til angitt verdi.
   * @param border Felles kantlinje
   * @return ny stil
   */
  def allBorders(border: Option[BorderLine]): Styles =
    this.copy(borderTop=border, borderBottom=border, borderLeft=border, borderRight=border)

  /**
   * Lag en kopi med "invers farge".
   * @return ny stil
   */
  def shaded(): Styles = this.copy(fgColor=Some(ColorShadeFg), bgColor=Some(ColorShadeBg))

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
    borderTop foreach { b => style.setBorderTop(b.thickness.asInstanceOf[Short])
                           style.setTopBorderColor(b.color.color)}
    borderBottom foreach { b => style.setBorderBottom(b.thickness.asInstanceOf[Short])
                           style.setBottomBorderColor(b.color.color)}
    borderLeft foreach { b => style.setBorderLeft(b.thickness.asInstanceOf[Short])
                           style.setLeftBorderColor(b.color.color)}
    borderRight foreach { b => style.setBorderRight(b.thickness.asInstanceOf[Short])
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
