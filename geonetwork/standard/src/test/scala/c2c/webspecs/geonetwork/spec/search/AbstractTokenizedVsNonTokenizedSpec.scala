package c2c.webspecs.geonetwork
package spec.search

import org.specs2.specification.Step
import org.specs2.execute.Result

trait AbstractTokenizedVsNonTokenizedSpec[SearchResult] {
  self: GeonetworkSpecification with AbstractSearchSpecification[SearchResult] =>
 def titleExtension:String
 def is =
    ("TokenizedVsNonTokenizedSpec"+titleExtension).title ^ Step(setup) ^
    "Import a metadata" ^ Step(setupMd) ^
    "Assert that the metadata is found when searching with a tokenized field like title and only a ${partial} title is used" ! searchTitle ^
    "Assert that the metadata is found when searching with a tokenized field like title and the ${split} title is used" ! searchTitle ^
    "Assert that the metadata is found when searching with a tokenized field like title and the ${full} title is used" ! searchTitle ^
    "Assert that the metadata is NOT found when searching with a nontokenized field like keyword and only the ${partial} keyword is used" ! searchKeyword ^
    "Assert that the metadata IS found when searching with a nontokenized field like keyword and the ${full} keyword is used" ! searchKeyword ^
                                                                   Step(tearDown)

  lazy val setupMd = importExtraMd(1, identifier=datestamp)
  def mdId = setupMd.head.id
  
  def search(params: (String, String)*): Int

  val searchTitle = (spec: String) => {
    val titles = extract1(spec) match {
      case "full" => "Title"+datestamp+" Entire World" :: Nil
      case "partial" => "Title"+datestamp :: Nil
      case "split" => "Title"+datestamp :: "Entire" :: "World" :: Nil
    }
    
    val params = titles.map("title" -> _) :+ ("_id" -> mdId)  
    val result = search(params : _*)

    result must_== 1
  }

  val searchKeyword = (spec: String) => {
    val (keywords, expectFound) = extract1(spec) match {
      case "full" => (Seq("Entire World"), 1)
      case "partial" => (Seq("World"), 0)
      case "split" => (Seq("Entire","World"), 0)
    }
    
    val params = keywords.map("keyword" -> _) :+ ("_id" -> mdId)  
    val result = search(params : _*)

    result must_== expectFound
  }
}