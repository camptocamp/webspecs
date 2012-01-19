package c2c.webspecs
import org.apache.http.client.methods.HttpGet

abstract class AbstractGetRequest[-In, +Out](uri:String,valueFactory:ValueFactory[In,Out],params:Param[In,String]*)
  extends AbstractRequest[In,Out](valueFactory) {
  override def request(in:In, uriResolver:UriResolver) = {
    val stringParams = params.map(p => p.name.encode -> p.value(in).encode)
    val resolvedUri = uriResolver(uri,stringParams)
    new HttpGet(resolvedUri)
  }
}
