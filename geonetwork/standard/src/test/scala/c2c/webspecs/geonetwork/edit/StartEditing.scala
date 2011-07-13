package c2c.webspecs
package geonetwork
package edit

import MetadataViews.MetadataView

object StartEditing {
  def apply(view:MetadataView = MetadataViews.simple):Response[IdValue] => StartEditing = response => StartEditing(response.value.id,view)
}
case class StartEditing(mdId:String,view:MetadataView)
  extends AbstractGetRequest[Any,EditValue]("metadata.edit",EditValueFactory, P("id", mdId),P("currTab", view.toString))

