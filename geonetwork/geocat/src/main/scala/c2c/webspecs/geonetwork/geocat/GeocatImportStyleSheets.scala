package c2c.webspecs.geonetwork
package geocat

import c2c.webspecs.geonetwork.ImportStyleSheets.ImportStyleSheet

object GeocatImportStyleSheets {
  case object GM03_V1 extends ImportStyleSheet("GM03-to-ISO19139CHE.xsl")
  case object GM03_V2 extends ImportStyleSheet("GM03_2-to-ISO19139CHE.xsl")
}
