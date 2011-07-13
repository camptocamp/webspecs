package c2c.webspecs
package geonetwork
package csw

trait DomainParam {val name:String}
object DomainProperties {
  abstract class DomainProperty(val name:String) extends DomainParam
  case object Title extends DomainProperty("Title")
  case object Subject extends DomainProperty("Subject")
  case object Owner extends DomainProperty("_owner")
  case object TopicCategory extends DomainProperty("topicCat")
  case object Type extends DomainProperty("Type")

}
