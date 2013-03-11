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
import c2c.webspecs.geonetwork._

object UploadThumbnailApp extends WebspecsApp {

  override def referenceSpecClass = classOf[GeocatSpecification]
  val urlsToTest = Array("tc-geocat-varnish.dev.camptocamp.net", "tc-geocat-apache.dev.camptocamp.net",
    "tc-geocat-elb.dev.camptocamp.net")
  urlsToTest.take(1).foreach { url =>
    implicit val resolver = new GeonetworkURIResolver() {
      override def baseURL = url
    }

    //    LoginRequest("admin","admin").execute()
    LoginRequest("admin", "Hup9ieBe").execute()(executionContext, resolver)

    //    val editValue = StartEditing().execute(Id("2")).value
    //    val editValue = StartEditing().execute(Id("659")).value
    val editValue = StartEditing().execute(Id("519463"))(executionContext, resolver).value
    val scaling = Some(ThumbnailScaling(180, true))
    val img = ResourceLoader.loadImageFromClassPath("/geonetwork/SwitzerlandSketch-4.png", classOf[AddRemoveOverviewSpec])

    val response = SetSmallThumbnail(editValue, img, scaling = scaling).execute()(executionContext, resolver)
    println(url + ": " + response.basicValue.responseCode + " " + response.basicValue.responseMessage)
    println(response.basicValue.allHeaders.mkString("\n"))
  }
}