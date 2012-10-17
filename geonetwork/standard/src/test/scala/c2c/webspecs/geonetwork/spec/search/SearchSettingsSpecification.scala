package c2c.webspecs.geonetwork.spec.search
import scala.xml.NodeSeq
import c2c.webspecs.ExecutionContext
import c2c.webspecs.GetRequest
import c2c.webspecs.XmlPostRequest
import c2c.webspecs.geonetwork.GeonetworkSpecification

trait SearchSettingsSpecification {
  self: GeonetworkSpecification =>
  lazy val getSearchSetting = ExecutionContext.withDefault{ implicit context =>
    config.adminLogin.execute()
    GetRequest("xml.config.get").execute().value.getXml
  }
  def doSetSearchSettings(settings:NodeSeq) = ExecutionContext.withDefault{ implicit context =>
    config.adminLogin.execute()
    XmlPostRequest("xml.config.set", <config>{getSearchSetting \ "site"}{settings}</config>).execute() must haveA200ResponseCode
  }
  def resetSearchSetting = doSetSearchSettings(getSearchSetting \ "requestedLanguage")
  /**
   * Valid values of only are 
   * "off", "prefer_locale", "prefer_docLocale"
   * "only_locale", "only_docLocale"
   */
  def setSearchSetting(only:String, sorted:Boolean, ignored:Boolean) = {
    doSetSearchSettings(<requestedLanguage>
    <only>{only}</only>
    <sorted>{sorted}</sorted>
    <ignored>{ignored}</ignored>
  </requestedLanguage>)
  }

}