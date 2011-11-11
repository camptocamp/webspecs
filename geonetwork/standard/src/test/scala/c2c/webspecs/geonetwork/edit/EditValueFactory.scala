package c2c.webspecs
package geonetwork
package edit

import c2c.webspecs.BasicValueFactory
import c2c.webspecs.BasicHttpValue

object EditValueFactory extends ValueFactory[Id, EditValue] {
  val self = this
  def fromCreateMd() = new ValueFactory[Any,EditValue] {
      def createValue[A <: Any, B >: EditValue]
		  (request:Request[A,B],
		   in:Any, 
		   rawValue:BasicHttpValue,
		   executionContext:ExecutionContext, 
		   uriResolver:UriResolver) = {
        val mdId = (rawValue.toXmlValue.getXml \\ "request" \ "id").text.trim
        self.createValue(request,IdValue(mdId,rawValue), rawValue, executionContext, uriResolver)
      }

  }

  def setId(mdId:String) = new ValueFactory[Any,EditValue] {
      def createValue[A <: Any, B >: EditValue]
		  (request:Request[A,B],
		   in:Any, 
		   rawValue:BasicHttpValue,
		   executionContext:ExecutionContext, 
		   uriResolver:UriResolver) = self.createValue(request,IdValue(mdId,rawValue), rawValue, executionContext, uriResolver)

  }
    
  
  
  def createValue[A <: Id, B >: EditValue]
		  (request:Request[A,B],
		   in:Id,
		   rawValue:BasicHttpValue,
		   executionContext:ExecutionContext, 
		   uriResolver:UriResolver) = new EditValue {
    protected def basicValue = rawValue
    lazy val id = in.id
    lazy val version = rawValue.toXmlValue.getXml \\ "info" \ "version" text
  }
}

