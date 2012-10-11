package c2c.webspecs.geonetwork.geocat
package spec.bugs

class AddRemoveOverviewSpec extends c2c.webspecs.geonetwork.spec.edit.AddRemoveOverviewSpec with GeocatSpecification{
	override def metadataToImport = "/geocat/data/bare.iso19139.che.xml"
}