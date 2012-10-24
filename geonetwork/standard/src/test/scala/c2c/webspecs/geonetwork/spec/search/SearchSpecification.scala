package c2c.webspecs
package geonetwork
package spec.search

import org.specs2.execute.Result

trait SearchSpecification extends AbstractSearchSpecification[XmlSearchValues] {
  self:GeonetworkSpecification =>
  def titleExtension = "Basic"
    
  def findCodesFromResults(records: XmlSearchValues) = {
    val recordIds = records.records map (_.infoValue("id"))
    recordIds flatMap idToLocalMap.get
  }

}