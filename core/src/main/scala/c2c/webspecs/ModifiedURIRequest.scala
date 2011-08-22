package c2c.webspecs
import java.net.URI

class ModifiedURIRequest[-In,+Out](newURI:String, wrapped:AbstractRequest[In,Out]) extends Request[In,Out] {
    def request(in:In) = {
      val base = wrapped.request(in)
      val uri = if(base.getURI.getQuery!=null) Config.resolveURI(newURI)+"?"+base.getURI().getQuery()
                else Config.resolveURI(newURI)
      base.setURI(new URI(uri))
      base
    }
    
   final def apply (in: In)(implicit context:ExecutionContext) = {
    val createdRequest = request(in)
    val httpResponse = context.execute(createdRequest)
    val basicValue = BasicHttpValue(httpResponse)
    val value = wrapped.valueFactory.createValue(wrapped,in,basicValue,context)
    val response = new BasicHttpResponse(basicValue,value)
    response
  }
}