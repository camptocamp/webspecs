package c2c.webspecs
package geonetwork

import xml.NodeSeq

/**
 * User: jeichar
 * Date: 1/19/12
 * Time: 4:23 PM
 */
case class XmlSearch(params: Seq[(String, String)] = Seq.empty)
  extends AbstractGetRequest("q", XmlSearchResultFactory, XmlSearchSupport.createParams(params):_*) {
  
  import XmlSearchSupport._
  def sortBy(field:Any, ascending:Boolean) = {
    val newParams = Seq('sortBy -> field) ++ (if(ascending) Seq('sortOrder -> 'reverse) else Seq.empty)
    
    update(this, false, newParams: _*)
  }

  def from(i: Int) = update(this, true, 'from -> i)
  def to(i: Int) = update(this, true, 'to -> i)
  def range(from: Int, to: Int) = update(this, true, 'from -> from, 'to -> to)
  def hitsPerPage(i: Int) = update(this, true, 'hitsperpage -> i)
  def fast(i: FastTypeEnum.Value) = update(this, true, 'fast -> i.toString)
  def search(newParams: (Any,Any)*) = update(this, false, newParams: _*)
  def withSummary = update(this, true, 'buildSummary -> true)
  def summaryOnly = update(this, true, 'buildSummary -> true, 'summaryOnly -> 1)
}

object FastTypeEnum extends Enumeration {
  val index, all = Value
  val fast = Value("true")
}
object XmlSearchSupport {
    def stringMap(params: Seq[(Any, Any)]) = {
      def string(e: Any) = e match {
          case s: Symbol => s.name
          case a => a.toString
      }
      params.map{e => (string(e._1) -> string(e._2))}
    }
  def update(searcher: XmlSearch, removeFirst:Boolean, newParams: (Any,Any)*) = {
    val filtered = searcher.params.filterNot(e => newParams.exists(_._1 == e._1)) 
    val all = filtered ++ stringMap(newParams)
    XmlSearch(all)
  }
  def createParams(params: Seq[(String, String)]) = {
    val finalParams = if (!params.exists(_._1 == "fast")) params :+ ("fast" -> "index") else params
    finalParams.toSeq.map(Param.stringMapping)
  }
}
object XmlSearchResultFactory extends BasicValueFactory[XmlSearchValues] {
  def createValue(rawValue: BasicHttpValue) = new XmlSearchValues(rawValue)
  
}

case class XmlSearchValues(rawValue:BasicHttpValue) {
  lazy val xml = XmlValueFactory.createValue(rawValue).getXml
  lazy val records = (xml \ "metadata").toList map (new XmlSearchValue(_))
  lazy val summary = (xml \ "summary").headOption
  lazy val count = (summary.flatMap (_.attribute("count").map(_.text)).headOption getOrElse "0").toInt
  lazy val size = records.size
  lazy val to = (xml.flatMap (_.attribute("to").map(_.text)).headOption getOrElse "0").toInt
  lazy val from = (xml.flatMap (_.attribute("from").map(_.text)).headOption getOrElse "0").toInt
}
class XmlSearchValue(xml:NodeSeq) {
  override def toString = title
  val uuid = infoValue("uuid")
  val defaultTitle = recordValue("defaultTitle")
  val title = if(recordValue("title").trim.isEmpty) recordValue("defaultTitle") else recordValue("title")

  def recordValue(name: String) = (xml \ name).headOption.map(_.text) getOrElse ""
  def infoValue(name: String) = (xml \ "info" \ name).headOption.map(_.text) getOrElse ""
}