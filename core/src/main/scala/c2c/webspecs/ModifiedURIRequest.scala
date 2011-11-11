package c2c.webspecs
import java.net.URI

class ModifiedURIRequest[-In,+Out](newURI:String, wrapped:AbstractRequest[In,Out]) extends Request[In,Out] {
    def request(in:In, uriResolver:UriResolver) = {
      val base = wrapped.request(in, uriResolver)
      val uri = if(base.getURI.getQuery!=null) Config.resolveURI(newURI)+"?"+base.getURI().getQuery()
                else Config.resolveURI(newURI)
      base.setURI(new URI(uri))
      base
    }
    
   final def execute (in: In)(implicit context:ExecutionContext, uriResolver:UriResolver) = {
    val createdRequest = request(in,uriResolver)
    val httpResponse = context.execute(createdRequest)
    val basicValue = BasicHttpValue(httpResponse)
    val value = wrapped.valueFactory.createValue(wrapped,in,basicValue,context,uriResolver)
    val response = new BasicHttpResponse(basicValue,value)
    response
  }
}