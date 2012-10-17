package c2c.webspecs
package debug

import c2c.webspecs.login.LoginRequest
import c2c.webspecs.geonetwork.edit.ThumbnailScaling
import c2c.webspecs.geonetwork.edit.SetSmallThumbnail
import c2c.webspecs.geonetwork.edit.StartEditing
import c2c.webspecs.ResourceLoader
import c2c.webspecs.WebspecsApp
import c2c.webspecs.geonetwork.geocat.spec.bugs.AddRemoveOverviewSpec
import c2c.webspecs.geonetwork.geocat.GeocatSpecification

object UploadThumbnailApp extends WebspecsApp {
  
  Properties.specClass = classOf[GeocatSpecification]
    LoginRequest("admin","Hup9ieBe").execute()
    
    val editValue = StartEditing().execute(Id("577189")).value
    val scaling = Some(ThumbnailScaling(180, true))
    val img = ResourceLoader.loadImageFromClassPath("/geonetwork/SwitzerlandSketch-4.png", classOf[AddRemoveOverviewSpec])
    val response = SetSmallThumbnail(editValue,img, scaling = scaling).execute()
    println(response.basicValue.responseCode)
    println(response.basicValue.responseMessage)
}