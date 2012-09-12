package c2c.webspecs
package geoserver
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.matcher.Matcher
import org.specs2.execute.Result
import _root_.scala.xml._
import scala.util.Random._

@RunWith(classOf[JUnitRunner])
class TimeFilterSpec extends GeoserverSpecification {

  def isImpl = 
    "Time Filter".title ^
    	"This Spec verifies that the application schema created for IGN works with time filters" ^
    	"${During} Filter can select a ${2} of the total records" ! periodTest ^
    	"${After} Filter can select a ${5} of the total records" ! periodTest ^
//    	"${AnyInteracts} Filter can select a ${5} of the total records" ! periodTest ^
    	"${Before}  Filter can select a ${3} of the total records" ! periodTest ^
    	"${Begins}  Filter can select a ${2} of the total records" ! periodTest ^
    	"${BegunBy} Filter can select a ${2} of the total records" ! periodTest ^
    	"${EndedBy} Filter can select a ${3} of the total records" ! periodTest(endTime="2012-01-01T00:00:00Z") ^
    	"${Ends}  Filter can select a ${3} of the total records" ! periodTest(endTime="2012-01-01T00:00:00Z") ^
//    	"${Meets} Filter can select a ${3} of the total records" ! periodTest(endTime="2012-01-01T00:00:00Z") ^
//    	"${MetBy}  Filter can select a ${3} of the total records" ! periodTest ^
//    	"${OverlappedBy}  Filter can select a ${3} of the total records" ! periodTest ^
//    	"${TContains}  Filter can select a ${3} of the total records" ! periodTest(endTime="2012-01-01T00:00:00Z") ^
//    	"${TOverlaps} Filter can select a ${3} of the total records" ! periodTest(endTime="2012-01-01T00:00:00Z") ^
    	"${TEquals} ${2000-01-01T00:00:00Z} Filter can select a ${3} of the total records" ! instantTest

  def periodTest(implicit startTime: String = "2010-01-01T00:00:00Z", endTime: String = "2010-12-01T00:00:00Z") = (spec:String) => {
    val (operation, expectedRecords) = extract2(spec)
    
    val children = Seq(<ValueReference>au:beginLifespanVersion</ValueReference>, { time(startTime, endTime) })
    val filter = Elem(null, operation, Null, TopScope, children: _*)

    val response = new GetFeatureRequest("au:AdministrativeBoundary", filter).execute()
    (response must haveA200ResponseCode) and
      (response.value.getXml \\ "member" must haveSize(expectedRecords.toInt))
  }
  
  val instantTest = (spec:String) => {
    val (operation, time, expectedRecords) = extract3(spec)
    
    val children = Seq(<ValueReference>au:beginLifespanVersion</ValueReference>, { instant(time) })
    val filter = Elem(null, operation, Null, TopScope, children: _*)

    val response = new GetFeatureRequest("au:AdministrativeBoundary", filter).execute()
    (response must haveA200ResponseCode) and
      (response.value.getXml \\ "member" must haveSize(expectedRecords.toInt))
  }
  
  def time(start: String, end: String) =
    <gml:TimePeriod gml:id="TP1">
      <gml:begin>
        { instant(start) }
      </gml:begin>
      <gml:end>
        { instant(end) }
      </gml:end>
    </gml:TimePeriod>

  def instant(time: String) =
    <gml:TimeInstant gml:id={ "TI" + nextInt }>
      <gml:timePosition>{ time }</gml:timePosition>
    </gml:TimeInstant>

}