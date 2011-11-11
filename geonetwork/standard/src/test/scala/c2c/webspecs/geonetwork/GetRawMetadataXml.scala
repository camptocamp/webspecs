package c2c.webspecs
package geonetwork

object GetRawMetadataXml 
    extends AbstractGetRequest[Id,MetadataValue]("xml.metadata.get", 
        SelfValueFactory[Id,MetadataValue](), 
        InP("id", (_:Id).id)) 
    with ValueFactory[Id,MetadataValue] {
    def createValue[A <: Id, B >: MetadataValue]
            (request:Request[A,B],
             in:Id,
             rawValue:BasicHttpValue,
             executionContext:ExecutionContext, 
             uriResolver:UriResolver) = {
    new MetadataValue {
      def basicValue = rawValue
      val id = in.id
    }
  }
  
}