package c2c.webspecs
package geonetwork

case object DeleteMetadata extends AbstractGetRequest[Id,IdValue](
  "metadata.delete",
  InputTransformerValueFactory[Id,IdValue]((in,raw) => IdValue(in.id,raw) ),
  InP("id", id => id.id))


