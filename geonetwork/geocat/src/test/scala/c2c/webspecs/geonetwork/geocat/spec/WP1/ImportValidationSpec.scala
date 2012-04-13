package c2c.webspecs
package geonetwork
package geocat
package spec.WP1

import csw._
import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ImportValidationSpec  extends GeocatSpecification {  def is =

  "This specification tests the behaviour of importing metadata with the validate option"      ^ Step(setup) ^
      "Importing a fully valid metadata will obviously import correctly"                       ! importFragment(true,  "metadata.iso19139.che.xml") ^
      "Importing an invalid metadata will result in a failed request"                          ! importFragment(false, "metadata.iso19139.che.invalid.xml") ^
      "Imported a valid metadata but that is not inspire compliate will be imported correctly" ! importFragment(true,  "metadata.iso19139-inspire-invalid.xml") ^ Step(tearDown)

                                                                              
  def importFragment(willImport:Boolean, filename:String) = {
    val request = ImportMetadata.defaults(uuid,"/geocat/data/"+filename,true,getClass)._2
    val response = request.execute()
    
    if(willImport) registerNewMd(Id(response.value.id))
    
    if(willImport) {
      response must haveA200ResponseCode
    } else {
        response must haveAResponseCode(500)
    }
  }
  
}