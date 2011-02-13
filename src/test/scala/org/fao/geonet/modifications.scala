package org.fao.geonet

import org.apache.http.client.methods.HttpRequestBase


trait Modification extends Function1[HttpRequestBase,Unit]
object NoModification extends Modification {
   def apply(request:HttpRequestBase) = ()
}