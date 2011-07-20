package c2c.webspecs

import org.apache.http.client.methods.HttpRequestBase

object RequestModification {
  def apply(mod:HttpRequestBase => Unit) = new RequestModification {
    def apply(v1: HttpRequestBase) = mod(v1)
  }
}
trait RequestModification extends Function1[HttpRequestBase,Unit]
