package c2c.webspecs
package debug

import geonetwork._
import c2c.webspecs.geonetwork.csw._
import c2c.webspecs.login.LoginRequest
import org.apache.http.entity.mime.content.StringBody
import scala.xml._
import scala.xml.transform._

object CopyRecordsApp extends WebspecsApp {
    LoginRequest("admin", "admin").execute()
    //  	val filter = PropertyIsEqualTo("hasLinkageURL", "y")
    val filter = PropertyIsEqualTo("keyword", "e-geo.ch geoportal")
    val res = CswGetRecordsRequest(filter.xml, resultType = ResultTypes.results, maxRecords = 1, url = "http://www.geocat.ch/geonetwork/srv/eng/csw").execute()

    val transformed = new RuleTransformer(RemoveResults)(res.value.getXml)
    for (md <- transformed \\  "CHE_MD_Metadata") {
      val uuid = (md \ "fileIdentifier" text).trim()
      println(md)
      println("Importing md: "+ uuid)
      val data = new StringBody(md.toString) {
        override def getFilename() = uuid+".xml" 
      }
      val importResponse = ImportMetadata(
        data,
        styleSheet = ImportStyleSheets.NONE,
        validate = false,
        groupId = "17").execute().value.getXml
        
      val id = (importResponse \\ "ok" text).split(";")(0)
      GetRequest("metadata.admin", "id" -> id, "_1_0" -> "on", "_1_1" -> "on").execute()
      println("new id = "+id)
  }	
}

object RemoveResults extends RewriteRule {
 override def transform(n:Node):Seq[Node] = 
   if(n.label == "response") return Nil
   else Seq(n)
   
  def stripXlink(n:Node) = n match {
     case e:Elem =>
	     val atts = n.attributes.filter(e => e.key != "href" && e.key != "role" && e.key != "show" && e.key != "xlink")
	     val kk = n.attributes.map(_.key)
	     e.copy(attributes = atts)
     case n => n
  }
}
