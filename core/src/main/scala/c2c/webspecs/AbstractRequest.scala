package c2c.webspecs
import org.apache.http.client.methods.HttpRequestBase

abstract class AbstractRequest[-In, +Out]
    (val valueFactory:ValueFactory[In,Out]) extends Request[In,Out] {
  def mapURI(uri:String) = new ModifiedURIRequest(uri,this)
  def request(in:In):HttpRequestBase
  
  final def apply (in: In)(implicit context:ExecutionContext) = {
    val createdRequest = request(in)
    val httpResponse = context.execute(createdRequest)
    val basicValue = BasicHttpValue(httpResponse)
    val value = valueFactory.createValue(this,in,basicValue,context)
    val response = new BasicHttpResponse(basicValue,value)
    response
  }
}

