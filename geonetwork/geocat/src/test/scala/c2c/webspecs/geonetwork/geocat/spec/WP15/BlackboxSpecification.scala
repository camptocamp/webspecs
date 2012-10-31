package c2c.webspecs
package geonetwork
package geocat
package spec.WP15

import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class GcoRecordBlackboxSpecification extends GeocatSpecification { def is =
  "This specification ensures that gco:Record can contain any type of data" ^ Step(setup) ^
  	"Import a metadata element with a gco:Record as xml" ^ Step(importMd) ^
  	"Verify that a show request shows the gco:Record ${original} XML" ! showXml ^
//  	"Verify that a edit request allows editing all the ${original} XML" ! editXml ^ endp^
//  	"Verify that new xml can be uploaded via the update edit service" ^ Step(updateXml) ^
//  	"Verify that a show request shows the gco:Record ${new} XML" ! showXml ^
//  	"Verify that a edit request allows editing all the ${new} XML" ! editXml ^
//  	"Verify that the record can be updated with a string via the update/edit service" ^ Step(updateString) ^
//  	"Verify that a show request shows the gco:Record string" ! showString ^
//  	"Verify that a edit request allows editing the string" ! editString ^ 
  	Step(tearDown)
  	
  lazy val importMd =
    super.importMd(1, "/geocat/data/gcoRecordExample.xml", datestamp, ImportStyleSheets.NONE).head
  def mdId = importMd
  
  val showXml = (spec: String) => {
    val showResponse = ShowMetadata(MetadataViews.dataQuality).execute(mdId)
    val md = showResponse.value.getText
    (showResponse must haveA200ResponseCode) and 
    	(md must contain ("<a><b>"+datestamp+"</b></a>"))
  }
  
}