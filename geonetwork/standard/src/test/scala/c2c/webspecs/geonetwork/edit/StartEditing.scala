package c2c.webspecs
package geonetwork
package edit

import MetadataViews.MetadataView

case class StartEditing(view:MetadataView = MetadataViews.simple, html:Boolean = false)
  extends AbstractGetRequest[Id,EditValue]("metadata.edit" + (if(html) "" else "!"),EditValueFactory, IdP("id"),P("currTab", view.toString))

