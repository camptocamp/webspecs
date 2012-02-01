package c2c.webspecs

import org.apache.http.entity.mime.MultipartEntity
import org.apache.http.entity.mime.content.ContentBody
import org.apache.http.client.methods.HttpPost

abstract class AbstractMultiPartFormRequest[In,Out](url:String, valueFactory:ValueFactory[In,Out], form:Param[In,ContentBody]*)
  extends AbstractRequest[In,Out](valueFactory) {

  require(form.size > 0, "At least one form element is required")

   override def request(in:In, uriResolver:UriResolver) = {
     val httppost = new HttpPost(uriResolver(url,Nil))

     val reqEntity = new MultipartEntity()
     form.foreach {part =>
       {
         val name: String = part.name
         val contentBody: ContentBody = part.value(in)
         Log.apply(Log.RequestMPForm, contentBody.toString())
         reqEntity.addPart(name, contentBody)
       }
     }
     httppost.setEntity(reqEntity)

     httppost
   }
}

