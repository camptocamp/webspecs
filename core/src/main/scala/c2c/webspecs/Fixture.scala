package c2c.webspecs

/**
 * A fixture created by [[c2c.webspecs.WebSpecsSpecification]]
 */
trait Fixture[C <: Config] {
  def create(config: C, context: ExecutionContext, uriResolver:UriResolver): Unit

  def delete(config: C, context: ExecutionContext, uriResolver:UriResolver): Unit
}