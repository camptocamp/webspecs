package c2c.webspecs.geonetwork

import c2c.webspecs.ExecutionContext
import org.specs2.Specification
import java.util.{UUID, Random}

trait Fixture {
  def create(config: GeonetConfig, context: ExecutionContext): Unit

  def delete(config: GeonetConfig, context: ExecutionContext): Unit
}

object GeonetFixture {
}