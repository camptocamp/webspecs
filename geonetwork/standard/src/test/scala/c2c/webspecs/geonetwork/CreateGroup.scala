package c2c.webspecs
package geonetwork

case class CreateGroup(group:Group)
  extends AbstractMultiPartFormRequest[Any,GroupValue](
    "group.update!",
    SelfValueFactory(),
    group.formParams:_*
  ) with BasicValueFactory[GroupValue] {
  def createValue(rawValue: BasicHttpValue) = {
    rawValue.toXmlValue.withXml{ data =>
      val groupRecord = data \\ "response" \\ "record" filter {rec => (rec \\ "name").text.trim == group.name}
      val id = (groupRecord \ "id").text
      if(id.isEmpty) Log(Log.Error, "An group id must be found, responseCode was: "+rawValue.responseCode)
      new GroupValue(id,group.name,group.description,group.email)
    }
  }
}