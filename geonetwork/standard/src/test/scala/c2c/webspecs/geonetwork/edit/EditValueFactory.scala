package c2c.webspecs
package geonetwork
package edit

import c2c.webspecs.BasicValueFactory
import c2c.webspecs.BasicHttpValue

object EditValueFactory extends BasicValueFactory[EditValue] {


  override def createValue(rawValue: BasicHttpValue) = apply(rawValue)

  def apply(rawValue: BasicHttpValue) = new EditValue {
    import XmlUtils._
    protected def basicValue = rawValue
    lazy val id = withXml( xml => lookupId(xml) )
    lazy val version = withXml( xml => lookupVersion(xml) )
  }

}
