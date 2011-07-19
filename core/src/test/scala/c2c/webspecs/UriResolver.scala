/*
 * Created by IntelliJ IDEA.
 * User: jeichar
 * Date: 2/15/11
 * Time: 2:38 PM
 */
package c2c.webspecs;
trait UriResolver {
  def apply(service: String, params: Seq[(String,String)]):String
}