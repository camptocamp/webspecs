package c2c.webspecs

import org.apache.http.client.methods.HttpRequestBase

object NoModification extends RequestModification {
   def apply(request:HttpRequestBase) = ()
}