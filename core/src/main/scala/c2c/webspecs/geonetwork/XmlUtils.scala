/*
 * Created by IntelliJ IDEA.
 * User: jeichar
 * Date: 2/25/11
 * Time: 4:14 PM
 */
package c2c.webspecs.geonetwork

import xml.NodeSeq
;
object XmlUtils {
 def extractId(li: String): Option[String] = {
    """.*id=(\d+).*?""".r.findFirstMatchIn(li) map {
      _.group(1)
    }
  }
  def inputValue(name:String)(implicit allInput:NodeSeq) = {
    val idInputEl = allInput filter {e => (e \ "@name").text == name}
    val values = idInputEl map {_.attribute("value").get.text}
    values.headOption getOrElse {
      throw new IllegalStateException("Expected to find an id input element. Does the response come from a request that returns a metadata.edit form? ")
    }
  }

  def lookupId(implicit allInput:NodeSeq) = inputValue("id")(allInput)
  def lookupVersion(implicit allInput:NodeSeq) = inputValue("version")(allInput)

}