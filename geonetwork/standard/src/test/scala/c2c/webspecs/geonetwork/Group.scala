package c2c.webspecs
package geonetwork

import org.apache.http.entity.mime.content.StringBody
import scalax.io.Codec

object Group {
  def apply(name:String, description:String="",email:String="") = {
    new Group(name,description, email)
  }
}
class Group(val name:String, val description:String="",val email:String="") {
  def formParams = List(
    P("name",new StringBody(name,Codec.UTF8.charSet)),
    P("description",new StringBody(description,Codec.UTF8.charSet)),
    P("email",new StringBody(email,Codec.UTF8.charSet))
  )

  override def toString: String = "Group(%s,%s,%s)".format(name,description,email)
}
