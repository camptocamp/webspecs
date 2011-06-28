package c2c.webspecs
package geonetwork
package geocat

import java.util.UUID
import c2c.webspecs.geonetwork.GeonetConfig
import c2c.webspecs.ExecutionContext

/**
 * Fixtures that only apply in Geocat
 */
object GeocatFixture {
  def format = new Fixture {
    val name = "WebSpecs"
    val version = UUID.randomUUID().toString
    private var _id:Int = -1

    def id = _id

    def delete(config: GeonetConfig, context: ExecutionContext) =
      (config.adminLogin then DeleteFormat(name, version))(None)(context)

    def create(config: GeonetConfig, context: ExecutionContext) = {
      val formats = (config.adminLogin then AddFormat(name, version) then ListFormats.setIn(name))(None)(context)
      _id = formats.value.find(_.version == version).get.id
    }
  }
}