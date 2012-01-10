package c2c.webspecs
package geonetwork
package edit

import MetadataViews.MetadataView

object StartEditing {
  def chain(view:MetadataView = MetadataViews.simple):Response[IdValue] => Request[Any,EditValue] = response => StartEditing(view).setIn(response.value)
}
case class StartEditing(view:MetadataView = MetadataViews.simple, html:Boolean = false)
  extends AbstractGetRequest[Id,EditValue]("metadata.edit" + (if(html) "" else "!"),EditValueFactory, IdP("id"),P("currTab", view.toString))

