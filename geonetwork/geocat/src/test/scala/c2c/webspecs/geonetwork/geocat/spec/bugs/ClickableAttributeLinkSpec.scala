package c2c.webspecs
package geonetwork
package geocat
package spec.bugs

import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ClickableAttributeLinkSpec extends GeocatSpecification{
	 def is = Step(setup) ^ sequential ^
			 "A metadata with links in the attribute description should have a hrefs in metadata.show"     ! hasLinks ^
			 Step (tearDown)

  def metadataToImport = "/geocat/data/non-clickable-att.xml"

  def hasLinks = {
    val md = importMd(1, metadataToImport, datestamp, ImportStyleSheets.NONE).head
    val xml = ShowMetadata(MetadataViews.contentInfo).execute(md).value.getHtml

    (xml \\ "a").find(a => (a @@ "href") == List("http://naturschutz.zh.ch/internet/bd/aln/ns/de/nsdaten/Datstandard/Pflegedaten.html")) must beSome
  }
}