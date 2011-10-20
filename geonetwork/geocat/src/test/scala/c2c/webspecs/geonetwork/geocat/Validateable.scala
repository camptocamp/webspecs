package c2c.webspecs.geonetwork.geocat

/**
 * Objects implementing this can have either validated or invalidated state
 */
trait Validateable {
  def validated:Boolean
}