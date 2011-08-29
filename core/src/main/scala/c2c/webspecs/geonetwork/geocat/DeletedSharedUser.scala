package c2c.webspecs
package geonetwork
package geocat

case class DeleteSharedUser(id:String, forceDelete:Boolean) 
    extends AbstractGetRequest(
        "shared.user.remove",
        DeletedSharedObjectIdFactory, 
        P("id",id), 
        SP("forceDelete", forceDelete)
    )