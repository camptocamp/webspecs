package c2c.webspecs
package geonetwork
package geocat

/**
 * Get MetadataXML with metadata information like xlinks etc...
 * 
 * @author jeichar
 */
object GetEditingMetadataXml 
	extends AbstractGetRequest[Id,MetadataValue]("xml.metadata.get", 
        SelfValueFactory[Id,MetadataValue](), 
        InP("id", (_:Id).id),
        SP("addEditing" -> true)
        ) 
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