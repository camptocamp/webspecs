package org.fao.geonet
package debug

/**
 * Created by IntelliJ IDEA.
 * User: jeichar
 * Date: 1/10/11
 * Time: 11:12 AM
 * To change this template use File | Settings | File Templates.
 */

object Misc extends Application {
  (Config.adminLogin then ListFormats()) {
    case r:ListFormatResponse => println(r.list)
  }
}
