package c2c.webspecs.geonetwork.geocat.shared

import SharedObjectTypes._

object SharedObjectHrefExtractor {
  val ServiceExtractor = """http://.+?/geonetwork/srv/[^/]*/([^?]+)?(.+)""".r
  val LocalServiceExtractor = """local://([^?]+)?(.+)""".r
  private def extractId(query: String) = {
    query.sliding(4).dropWhile(_.take(3) != "id=").
      drop(3).
      takeWhile(chars => chars(0) != '&' || chars == "&amp;").
      map(_.head).
      mkString
  }
  /**
   * extract a SharedObject from an xlink href
   */
  def unapply(href: String): Option[SharedObject] = {
    val (service, query) = if (href startsWith "local://") {
      val LocalServiceExtractor(service, query) = href
      (service, query)
    } else {
      val ServiceExtractor(service, query) = href
      (service, query)
    }
    val obj = service match {
      case "xml.user.get" =>
        val id = extractId(query)
        SharedObject(id, Some(href), "Unknown", contacts)
      case "xml.keyword.get" =>
        val id = extractId(query)
        SharedObject(id, Some(href), "Unknown", keywords)
      case "xml.extent.get" =>
        val id = extractId(query)
        SharedObject(id, Some(href), "Unknown", keywords)
      case "xml.format.get" =>
        val id = extractId(query)
        SharedObject(id, Some(href), "Unknown", keywords)
    }

    Some(obj)
  }
}