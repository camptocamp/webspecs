package c2c.webspecs.geonetwork

import c2c.webspecs.ExecutionContext

trait Fixture {
  def create(config: GeonetConfig, context: ExecutionContext): Unit

  def delete(config: GeonetConfig, context: ExecutionContext): Unit
}

object Fixture {
  val Format = new Fixture {
    val name = "WebSpecs"
    val version = "V_1"

    def delete(config: GeonetConfig, context: ExecutionContext) = ()//config.adminLogin then AddFormat.setIn(name, version)

    def create(config: GeonetConfig, context: ExecutionContext) = config.adminLogin then DeleteFormat(name, version)
  }
}