package c2c.webspecs.geonetwork
package geocat

/**
 * Contain some constants that don't fit in an enumeration or is too much work to put in one
 */
object GeocatConstants {
  final val INSPIRE_THESAURUS = "external.theme.inspire-theme"
  final val GEMET_THESAURUS = "external._none_.gemet"
  final val GEOCAT_THESAURUS = "local._none_.geocat.ch"
  final val NON_VALIDATED_THESAURUS = "local._none_.non_validated"
  final val KEYWORD_NAMESPACE = "http://custom.shared.obj.ch/concept#"
  final val HREF_NON_VALIDATED_ROLE_STRING = "http://www.geonetwork.org/non_valid_obj"
  final val GM03_TO_CHE_STYLESHEET = new ImportStyleSheets.ImportStyleSheet("GM03-to-ISO19139CHE.xsl"){}
  final val GM03_2_TO_CHE_STYLESHEET = new ImportStyleSheets.ImportStyleSheet("GM03_2-to-ISO19139CHE.xsl"){}
}