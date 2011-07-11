package c2c.webspecs
import org.apache.http.client.methods.HttpRequestBase

abstract class AbstractRequest[-In, +Out]
    (valueFactory:ValueFactory[In,Out]) extends Request[In,Out] {
  def request(in:In):HttpRequestBase
  final def apply (in: In)(implicit context:ExecutionContext) = {
    val httpResponse = context.execute(request(in))
    val basicValue = BasicHttpValue(httpResponse)
    val value = valueFactory.createValue(this,in,basicValue,context)
    val response = new BasicHttpResponse(basicValue,value)
    response
  }
}
