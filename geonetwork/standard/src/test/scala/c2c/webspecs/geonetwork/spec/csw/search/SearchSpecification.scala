package c2c.webspecs
package geonetwork
package spec.csw.search

import java.util.Date
import scala.xml.NodeSeq
import org.specs2.execute.Result
import c2c.webspecs.geonetwork.spec.search.AbstractSearchSpecification

trait SearchSpecification extends AbstractSearchSpecification[XmlValue] {
  self:GeonetworkSpecification =>
  def titleExtension = "Csw"

  def findCodesFromResults(xml:XmlValue) = {
      val records = xml.getXml \\ "Record"

      val recordIds = records \ "info" \ "id" map (_.text.trim)
      recordIds flatMap idToLocalMap.get
  }
  
}