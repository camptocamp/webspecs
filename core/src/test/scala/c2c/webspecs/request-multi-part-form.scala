package c2c.webspecs

import org.apache.http.entity.mime.MultipartEntity
import org.apache.http.entity.mime.content.ContentBody
import org.apache.http.client.methods.HttpPost

abstract class MultiPartFormRequest[In,Out](url:String, valueFactory:ValueFactory[In,Out], form:(String,ContentBody)*)
  extends AbstractRequest[In,Out](valueFactory) {

  require(form.size > 0, "At least one form element is required")

   def request(in:In) = {
     val httppost = new HttpPost(url)

     val reqEntity = new MultipartEntity()
     form.foreach {part =>
       reqEntity.addPart(part._1,part._2)
     }
     httppost.setEntity(reqEntity)

     httppost
   }

}