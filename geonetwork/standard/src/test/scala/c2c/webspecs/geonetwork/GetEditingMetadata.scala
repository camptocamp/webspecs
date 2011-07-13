package c2c.webspecs
package geonetwork

case object GetEditingMetadata extends AbstractGetRequest[Id,IdValue](
  "metadata.ext.edit.data",
  InputTransformerValueFactory((in,basic) =>
    new IdValue with XmlValue {
      protected def basicValue = basic
      def id = in.id
    }
  ),
  InP("id", _.id))
