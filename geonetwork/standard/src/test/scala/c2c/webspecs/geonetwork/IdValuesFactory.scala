package c2c.webspecs
package geonetwork

object IdValuesFactory {
  object FromImportOrCreateResult extends BasicValueFactory[IdValue] {
    override def createValue(rawValue: BasicHttpValue) =
      new XmlValue with IdValue {
        val basicValue = rawValue
        lazy val id = withXml { xml =>
          // early versions < 2.6
          val okNode = (xml \\ "ok").headOption

          // more recent versions > 2.6
          val idNode = (xml \\ "id").headOption

          val nodes = (okNode orElse idNode).map { _.text.split(";").map { _.trim }.filter { _.nonEmpty } }.toList.flatten

          nodes.headOption getOrElse {
            throw new IllegalStateException("Expected to find an id or ok element. Does the response come from a create or import request?\n"+xml)
          }
        }
      }
  }
  object FromEditResult extends BasicValueFactory[IdValue] {
    override def createValue(rawValue: BasicHttpValue) =
      new XmlValue with IdValue {
        val basicValue = rawValue
        lazy val id = withXml { xml =>
          val allInput = xml \\ "input"
          val idInputEl = allInput filter { e => (e \ "@name").text == "id" }
          val values = idInputEl map { _.attribute("value").get.text }
          values.headOption getOrElse {
            throw new IllegalStateException("Expected to find an id input element. Does the response come from a request that returns a metadata.edit form? ")
          }
        }
      }
  }
}