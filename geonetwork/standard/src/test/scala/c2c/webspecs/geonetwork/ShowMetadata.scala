package c2c.webspecs
package geonetwork

import MetadataViews._

case class ShowMetadata(view:MetadataView = MetadataViews.simple)
  extends AbstractGetRequest[Id,IdValue](
    "metadata.show",
    InputTransformerValueFactory( (in,basic) =>
      new XmlValue with IdValue {
        protected def basicValue = basic
        def id = in.id
      }
    ),
    InP("id", _.id),
    P("currTab", view.toString))
