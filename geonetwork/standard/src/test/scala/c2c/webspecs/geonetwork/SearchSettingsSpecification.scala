package c2c.webspecs.geonetwork
import scala.xml.NodeSeq
import c2c.webspecs.ExecutionContext
import c2c.webspecs.GetRequest
import c2c.webspecs.XmlPostRequest

trait SearchSettingsSpecification {
  self: GeonetworkSpecification =>
  lazy val getSearchSetting = ExecutionContext.withDefault{ implicit context =>
    config.adminLogin.execute()
    GetRequest("xml.config.get").execute().value.getXml
  }
  def doSetSearchSettings(settings:NodeSeq) = ExecutionContext.withDefault{ implicit context =>
    config.adminLogin.execute()
    val data: NodeSeq = <config>{getSearchSetting \ "site"}{settings}</config>
    XmlPostRequest("xml.config.set", data).execute() must haveA200ResponseCode
  }
  def resetSearchSetting = doSetSearchSettings((getSearchSetting \ "requestedLanguage") : NodeSeq)
  /**
   * Valid values of only are 
   * "off", "prefer_locale", "prefer_docLocale"
   * "only_locale", "only_docLocale"
   */
  def setSearchSetting(only:String, sorted:Boolean, ignored:Boolean) = {
    val data: NodeSeq = 
  <requestedLanguage>
    <only>{only}</only>
    <sorted>{sorted}</sorted>
    <ignored>{ignored}</ignored>
  </requestedLanguage>
    doSetSearchSettings(data)
  }

}