package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2.specification.Step
import c2c.webspecs.{XmlValue, Response}
import geonetwork.UserRef
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import c2c.webspecs.geonetwork.edit.AddSites
import c2c.webspecs.geonetwork.edit.StartEditing
import c2c.webspecs.geonetwork.edit.EndEditing
import org.specs2.specification.Fragments
import org.specs2.execute.Result
import org.specs2.specification.so

@RunWith(classOf[JUnitRunner]) 
class ContactsMatchSpec extends GeocatSpecification { def is = 
  "Reusable contacts must be correctly matched".title 												^ Step(setup) ^ 
  "Imports a metadata which contains the same contact twice"   										^ Step(ImportMdId)  ^
  "only one user with ${firstname"+uuid+"}"                                                         ! onlyOneUser ^ 
                                                           											  Step(tearDown)


  lazy val ImportMdId = {
    val importRequest = ImportMetadata.defaults(uuid,"/geocat/data/contact_has_repeated_contact.xml",false,getClass,GeocatConstants.GM03_2_TO_CHE_STYLESHEET)._2
    
    val id = importRequest.execute().value.id
    registerNewMd(Id(id))
    id
  }

  val onlyOneUser = (s:String) => {
    val searchTerm = extract1(s)
    
    val foundUsers = GeocatListUsers.execute(searchTerm).value
    
    foundUsers must haveSize (1)
  }
}