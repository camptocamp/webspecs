package c2c.webspecs
package geonetwork
package edit

import MetadataViews.MetadataView

case class StartEditing(view:MetadataView = MetadataViews.simple)
  extends AbstractGetRequest[Id,EditValue]("metadata.edit!",EditValueFactory, IdP("id"),P("currTab", view.toString))
case class StartEditingHtml(view:MetadataView = MetadataViews.simple)
extends AbstractGetRequest[Id,EditValue]("metadata.edit",EditValueFactory, IdP("id"),P("currTab", view.toString))

