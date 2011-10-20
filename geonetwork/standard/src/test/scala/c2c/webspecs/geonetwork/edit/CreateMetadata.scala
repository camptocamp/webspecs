package c2c.webspecs
package geonetwork
package edit

case class CreateMetadata(constants:GeonetConfig, templateId:String)
  extends AbstractGetRequest[Any,EditValue]("metadata.create!",EditValueFactory.fromCreateMd(),P("group", constants.groupId), P("id", templateId))
