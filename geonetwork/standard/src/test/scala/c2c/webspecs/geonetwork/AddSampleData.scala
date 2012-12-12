package c2c.webspecs
package geonetwork

/**
 * Add sample data for all schemas with the same UUID
 * User: jeichar
 * Date: 1/19/12
 * Time: 6:27 PM
 */
object AddAllSampleData extends AbstractGetRequest(
  "metadata.samples.add",
  XmlValueFactory,
  SP('uuidAction -> 'nothing),
  SP('file_type -> 'mef),
    SP('schema -> "csw-record,dublin-core,fgdc-std,iso19110,iso19115,iso19139"))

case class AddSampleData(schemas: String*) extends AbstractGetRequest(
  "metadata.samples.add",
  XmlValueFactory,
  SP('uuidAction -> 'nothing),
  SP('file_type -> 'mef),
  SP('schema -> schemas.mkString(",")))