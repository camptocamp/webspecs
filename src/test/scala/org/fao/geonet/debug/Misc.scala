package org.fao.geonet
package debug

object Misc extends Application {
  (Config.adminLogin then ListFormats()) {
    case r:ListFormatResponse => println(r.list)
  }
}
