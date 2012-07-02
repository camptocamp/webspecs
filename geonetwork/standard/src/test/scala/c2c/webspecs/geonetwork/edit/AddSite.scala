package c2c.webspecs
package geonetwork
package edit

object AddSites {
  val gmd = "gmd"
  abstract class AddSite(prefix:String) {
    def name = getClass.getSimpleName.split("\\$").last

    override def toString = prefix+":"+name
  }


  abstract class ExtentAddSite(prefix:String) extends AddSite(prefix)
  abstract class ContactAddSite(prefix:String) extends AddSite(prefix)
  abstract class FormatAddSite(prefix:String) extends AddSite(prefix)
  abstract class KeywordAddSite(prefix:String) extends AddSite(prefix)

  object contact extends ContactAddSite(gmd)
  object userContactInfo extends ContactAddSite(gmd)
  object distributorContact extends ContactAddSite(gmd)
  object citedResponsibleParty extends ContactAddSite(gmd)
  object processor extends ContactAddSite(gmd)
  object pointOfContact extends ContactAddSite(gmd)

  object distributionFormat extends FormatAddSite(gmd)
  object resourceFormat extends FormatAddSite(gmd)

  object descriptiveKeywords extends KeywordAddSite(gmd)

  object extent extends ExtentAddSite(gmd)
  object sourceExtent extends ExtentAddSite(gmd)

}