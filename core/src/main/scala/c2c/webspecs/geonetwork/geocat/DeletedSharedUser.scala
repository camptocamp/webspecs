package c2c.webspecs
package geonetwork
package geocat

case class DeleteSharedUser(id:String) extends AbstractGetRequest("shared.user.remove",DeletedSharedObjectIdFactory, P("id",id))