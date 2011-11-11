package c2c.webspecs
package geonetwork
package edit

import AddSites._
import scala.xml.Node

abstract class AbstractAddRequest(_serv:String, nodeRef:EditValue => String, addSite:AddSite, extraFields:Param[EditValue,String]*)
	extends Request[EditValue,AddValue] {
  def execute(in: EditValue)(implicit context: ExecutionContext, uriResolver:UriResolver) = {
    
    val addRequest = FormPostRequest(_serv,
    extraFields.map{p => p.name ->  p.value(in)} ++ 
    List("id" -> in.id,
	    "version" -> in.version,
	    "ref" -> nodeRef(in),
	    "name" -> addSite.toString,
	    "role" -> "embed",
	    "schema" -> "iso19139.che",
	    "currTab" -> "complete"):_*
	   )
	    
    val addResponse = addRequest.execute() 
    val value = new AddValue(in, addResponse.basicValue, extractElementXmlFromResponse(addResponse.value.getXml))
    new BasicHttpResponse(addResponse.basicValue,value)
  }
  
  def extractElementXmlFromResponse(responseXml:Node):Node
  
}

/**
 * @deprecated
 */
abstract class Add(_serv:String, id:String, editVersion:String, nodeRef:String, addSite:AddSite, extraFields:(String,Any)*)
  extends Request[EditValue,AddValue] {
  val addRequest = FormPostRequest(_serv,
    extraFields.map{case (key,value) => (key,value.toString)} ++ List("id" -> id,
    "version" -> editVersion,
    "ref" -> nodeRef,
    "name" -> addSite.toString,
    "role" -> "embed",
    "schema" -> "iso19139.che",
    "currTab" -> "complete"):_*)

  val beforeMetadata = GetMetadataXml().setIn(Id(id))
  val afterMetadata = GetMetadataXml().setIn(Id(id))


  def execute(in: EditValue)(implicit context: ExecutionContext, uriResolver:UriResolver) = {
    val before = beforeMetadata.execute()
    val add = addRequest.execute()
    val after = afterMetadata.execute()

    val (newElement, newXml) = before.value.withXml { beforeXml =>
      after.value.withXml { afterXml =>
        val beforeElems = (beforeXml \\ addSite.name)
        val afterElems = (afterXml \\ addSite.name).toList
        val newElem = afterElems filterNot {beforeElems contains _}

        if(newElem.size != 1){
          /*val writer = new BufferedWriter(new FileWriter("c:/tmp/"+id+addSite.name+".log"))
          writer.write(">>>>>>>>>>>>>>>>>>>>>>>>>Before:>>>>>>>>>>>>>>>>>>>>>>>>>\n")
          writer.write(beforeXml.toString)
          writer.write("-------------------------After:---------------------------\n")
          writer.write(afterXml.toString)
          writer.write("<<<<<<<<<<<<<<<<<<<<<<<<Finished:<<<<<<<<<<<<<<<<<<<<<<<<<\n")
          writer.close   */
        }

        (newElem, afterXml)
      }
    }

    assert(newElement.size == 1, "Expected there to be 1 new element but instead there was "+newElement.size)

    val newEditValue = EditValueFactory.createValue(this,in,add.basicValue, context, uriResolver)
    val value = new AddValue(newEditValue, add.basicValue, newElement.head)
    new BasicHttpResponse(add.basicValue,value)
  }
}