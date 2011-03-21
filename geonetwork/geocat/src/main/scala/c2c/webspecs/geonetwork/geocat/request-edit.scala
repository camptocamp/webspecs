package c2c.webspecs
package geonetwork
package geocat

import AddSites.{AddSite,ExtentAddSite}
import XmlUtils.{lookupId,lookupVersion}


object StandardSharedExtents {
  abstract class StandardSharedExtent(val typeName:String,val id:String)
  case object KantonBern extends StandardSharedExtent("gn:kantoneBB", "2")
}


abstract class revisionExtent extends ExtentAddSite("che")

object AddExtentXLink {
  def apply(extent: StandardSharedExtents.StandardSharedExtent,
            withPolygon: Boolean,
            addSite: ExtentAddSite = AddSites.extent) = (response: Response[XmlValue]) => {
    response.value.withXml {
      md =>
        implicit val allInput = md \\ "input"
        val xlink = Config.resolveURI("xml.extent.get",
          "wfs" -> "default",
          "format" -> (if (withPolygon) "gmd_complete" else "gmd_bbox"),
          "typename" -> extent.typeName,
          "id" -> extent.id,
          "extentTypeCode" -> "true")

        val nodeRef = XLink.lookupXlinkNodeRef(addSite.toString)(md)
        new AddXLink(lookupId, lookupVersion, xlink, nodeRef, addSite)
    }
  }
}

class AddXLink(id:String, editVersion:String, xlink:String, nodeRef:String, addSite:AddSite)
  extends Add("metadata.xlink.add", id, editVersion, nodeRef, addSite, "href" -> xlink)
