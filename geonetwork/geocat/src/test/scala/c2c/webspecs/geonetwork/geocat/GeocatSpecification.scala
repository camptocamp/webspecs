package c2c.webspecs
package geonetwork
package geocat

import scala.xml.NodeSeq

abstract class GeocatSpecification extends GeonetworkSpecification {

  def deleteMetadataAndSharedObjects(id:IdValue,xml:NodeSeq) = {
    (config.adminLogin then DeleteMetadata.setIn(id))(None) // delete metadata

    val allHrefs = xml \\ "_" filter (n => (n @@ "xlink:href" nonEmpty))

    val newLinks = xml \\ "_" filter (n => (n @@ "xlink:href" nonEmpty) && (n.text contains uuid))
    for (href <- newLinks \@ "xlink:href") {
      val obj = SharedObjectHrefExtractor.unapply(href).get
      DeleteSharedObject(obj.id,obj.objType)(None)
    }
  }
}