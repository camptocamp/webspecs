package c2c.webspecs
package geonetwork
package regions

case class Region(id:String, label:LocalisedString, categoryId:String, categoryLabel: LocalisedString, bbox: Bbox, hasGeom: Boolean)
