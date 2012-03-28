package c2c.webspecs
package geonetwork

case object GroupValueListFactory extends BasicValueFactory[List[GroupValue]] {
   def createValue(rawValue: BasicHttpValue) = {
    rawValue.toXmlValue.withXml{ data =>
      (data \\ "response" \ "record").toList map {
        groupRecord=>
          if(groupRecord.isEmpty) Log(Log.Error, "An group record must be found, responseCode was: "+rawValue.responseCode)
          val id = (groupRecord \ "id").text
          val name = (groupRecord \ "name").text
          val email = (groupRecord \ "description").text
          val description = (groupRecord \ "email").text
          new GroupValue(id,name,description,email)
      }
    }
  }
}