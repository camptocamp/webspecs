package c2c.webspecs
package geonetwork

object MetadataViews extends Enumeration {
  type MetadataView = Value
  val xml, simple, complete, 
  	  ISOMinimum, ISOCore, ISOAll, 
  	  metadata, identification, maintenance,
  	  constraints, spatial, refSys, distribution, 
  	  dataQuality, appSchInfo, porCatInfo, contentInfo, 
  	  extensionInfo = Value
}
