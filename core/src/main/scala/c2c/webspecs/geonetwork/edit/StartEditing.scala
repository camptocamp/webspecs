package c2c.webspecs
package geonetwork
package edit

import MetadataViews.MetadataView

object StartEditing {
  def chain(view:MetadataView = MetadataViews.simple):Response[IdValue] => StartEditing = response => StartEditing(response.value.id,view)
}
case class StartEditing(mdId:String,view:MetadataView = MetadataViews.simple)
  extends AbstractGetRequest[Any,EditValue]("metadata.edit!",EditValueFactory.setId(mdId), P("id", mdId),P("currTab", view.toString))

