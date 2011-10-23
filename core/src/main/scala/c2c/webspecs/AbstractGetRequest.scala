package c2c.webspecs
import org.apache.http.client.methods.HttpGet

abstract class AbstractGetRequest[-In, +Out](uri:String,valueFactory:ValueFactory[In,Out],params:Param[In,String]*)
  extends AbstractRequest[In,Out](valueFactory) {
  def request(in:In) = {
    val stringParams = params.map(p => p.name -> p.value(in))
    val resolvedUri = Config.resolveURI(uri,stringParams:_*)
    new HttpGet(resolvedUri)
  }
}
