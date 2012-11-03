package c2c.webspecs
package geonetwork

/**
 * Configure geonetwork's thread pool to not add tasks to thread pool but instead execute them in calling thread.
 *
 * Required for tests
 *
 * User: jeichar
 * Date: 1/19/12
 * Time: 9:07 PM
 */
case class SetSequentialExecution(value:Boolean) extends AbstractGetRequest("system.properties.set", XmlValueFactory, SP('name -> "geonetwork.sequential.execution"), SP('value -> value))