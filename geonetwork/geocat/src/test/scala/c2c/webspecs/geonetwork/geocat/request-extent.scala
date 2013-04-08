package c2c.webspecs
package geonetwork
package geocat

import shared._
import c2c.webspecs.AbstractGetRequest
import java.net.URL
import xml.NodeSeq

object Extents {
  abstract class TypeName(val name:String) {
    override def toString: String = name
  }
  case object NonValidated extends TypeName("gn:non_validated")
  case object Countries extends TypeName("gn:countries")
  case object Gemeinden extends TypeName("gn:gemeindenBB")
  case object Kantone extends TypeName("gn:kantoneBB")
  case object Validated extends TypeName("gn:xlinks")

  val AllTypeNames = Seq(NonValidated, Countries, Validated,Gemeinden, Kantone)

  abstract class Property(val name:String) {
    override def toString: String = name
  }
  case object IdProperty extends Property("id")
  case object DescProperty extends Property("desc")
}

import Extents._
/**
 * Search for an Extent
 */
case class SearchExtent(numResults:Int = 25,
                        property:Property = DescProperty,
                        format:ExtentFormat.Value = ExtentFormat.gmd_bbox,
                        typeName:Seq[Extents.TypeName] = AllTypeNames)
  extends AbstractGetRequest[String,List[ExtentSummary]]("extent.search.list!", SelfValueFactory[String,List[ExtentSummary]],
    IdP("pattern"),
    SP("numResults", numResults),
    SP("property", property),
    SP("typename", typeName mkString ","),
    SP("format", format) )
  with BasicValueFactory[List[ExtentSummary]] {
  def createValue(rawValue: BasicHttpValue): List[ExtentSummary] = {
    rawValue.toXmlValue.withXml{
      xml =>
        (xml \\ "feature").toList map {feature =>
          val id = feature \\ "@id" text
          val href = feature \\ "@href" text
          val desc = parseLanguages(feature \\ "desc")
          val validated = !href.contains("typename=gn:non_validated")
          val fullHref = if(href startsWith "local://") href.replace("local://","http://"+Properties.testServer+"/") else href
          ExtentSummary(id,new URL(fullHref), desc,validated)
        }
    }
  }

  private def parseLanguages(node:NodeSeq) = {
    val values = (node \ "_").toList map {n => n.label.toLowerCase -> n.text}
    LocalisedString(Map(values:_*))
  }
}

case class ExtentSummary(id:String, href:URL, desc:LocalisedString, validated:Boolean)

object ExtentFormat extends Enumeration {
  val gmd_bbox, gmd_polygon, gmd_complete = Value
}

case class DeleteExtent(typeName:Extents.TypeName, id:String, forceDelete:Boolean) extends AbstractGetRequest("xml.extent.delete",
    new DeletedSharedObjectIdFactory(),
    SP("typename" -> typeName),
    SP("id" -> id),
    SP("testing" -> true),
    SP("forceDelete", forceDelete)
  )