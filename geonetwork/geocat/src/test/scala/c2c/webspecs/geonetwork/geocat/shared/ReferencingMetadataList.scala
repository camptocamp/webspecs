package c2c.webspecs.geonetwork.geocat.shared

import c2c.webspecs.BasicHttpValue
import scala.collection.SeqProxy
import c2c.webspecs.BasicValueFactory
import c2c.webspecs.XmlValueFactory
import c2c.webspecs.XmlValue

/**
 * Result of the ListReferencingMetadata requests.  Is the list of metadata that references the shared object
 */
class ReferencingMetadataList(val basicValue: BasicHttpValue,
  val self: List[ReferencingMetadata]) extends SeqProxy[ReferencingMetadata] with XmlValue

object ReferencingMetadataListFactory extends BasicValueFactory[ReferencingMetadataList] {

  override def createValue(rawValue: BasicHttpValue) = {
    val xmlValue = XmlValueFactory.createValue(rawValue)
    val list = xmlValue.withXml {
      xml =>
        (xml \\ "record").toList map {
          result =>
            val id = (result \\ "id" text).toInt
            val title = (result \\ "title" text)
            val ownerName = (result \\ "name" text)
            val email = (result \\ "email" text)
            ReferencingMetadata(id, title, ownerName, email)
        }
    }

    new ReferencingMetadataList(rawValue, list)
  }
}